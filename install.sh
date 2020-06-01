#!/bin/bash

function mysql-settings(){
	read -p "Введите пароль для root mysql: " root_pass

	read -p "Введите имя БД mysql для приложения: " dbname
	while [ -z $dbname ]
	do
		echo "Имя не может быть пустым. Попробуйте ещё раз"
		read -p "Введите имя БД mysql для приложения: " dbname
	done

	read -p "Введите имя пользователя для $dbname: " dbuser
	while [ -z $dbuser ]
	do
		echo "Имя не может быть пустым. Попробуйте ещё раз"
		read -p "Введите имя пользователя для $dbname: " dbuser
	done

	read -p "Введите пароль пользователя для $dbuser: " dbpass
	while [ -z $dbpass ]
	do
		echo "Имя не может быть пустым. Попробуйте ещё раз"
		read -p "Введите имя пользователя для $dbname: " dbpass
	done
	# Скрипт аналогичен действиям mysql_secure_installation.deb
	chmod +x secure-mysql.sh
	./secure-mysql.sh $root_pass
	# Создание БД и пользователя
	mysql --user='root' --password='$root_pass'<<EOF
CREATE DATABASE ${dbname};
CREATE USER '${dbuser}'@'localhost' IDENTIFIED BY '${dbpass}';
GRANT ALL PRIVILEGES ON ${dbname}.* TO '${dbuser}'@'localhost';
EOF
	systemctl restart mysql.service
}

function nginx-settings(){
	conf_path="/etc/nginx/conf.d/app.conf"
	path_cert="${cur_dir}/ssl/private.crt"
	path_cert_key="${cur_dir}/ssl/private.key"
	answer=0
	if [[ -r $path_cert && -r $path_cert_key ]]; then	
		while true; do
		read -p "Использовать сертификат по-умолчанию (в папке ssl)[y/n]: " yn
	    case $yn in
	        [Yy]* ) answer=1;break;;
	        [Nn]* ) answer=0;break;;
	        * ) echo "Введите y или n";;
	    esac
		done
	fi

	# Ввод путей path_cert и path_cert_key
	if [[ answer -eq 0 ]]; then	
		read -p "Введите полный путь до файла-сертификата (.crt, .cer и т.п.): " path_cert
		while [ -r $path_cert ]
		do
			echo "Такого файла не существует или нет прав на чтение. Попробуйте ещё раз"
			read -p "Введите полный путь до файла-сертификата: " path_cert
		done

		read -p "Введите путь до файла c ключами сертификата (.key): " path_cert_key
		while [ -r $path_cert_key ]
		do
			echo "Такого файла не существует или нет прав на чтение. Попробуйте ещё раз"
			read -p "Введите полный путь до файла c ключами сертификата (.key): " path_cert_key
		done
	fi
	
	# Ввод server_name для nginx
	read -p "Введите ЧЕРЕЗ ПРОБЕЛ server_name для nginx (Домены, по которым будет доступно приложение): " domains
	while [ -z $domains ]
	do
		echo "Введена пустая строка. Попробуйте ещё раз"
		read -p "Введите ЧЕРЕЗ ПРОБЕЛ server_name для nginx (Домены, по которым будет доступно приложение)" domains
	done	
	
	# Создать конфиг
	touch ${conf_path}

	# Запись в конфиг
	cat > $conf_path <<EOF
	server { 
		#Порт приложения
		listen 80; 
		listen [::]:80; 
		#Включим ssl
		listen 443 ssl http2; 
		listen [::]:443 ssl http2; 
		# Домены, по которым доступно наше приложение
		server_name ${domains};
		# Путь до сертификата и его ключа
		ssl_certificate '${path_cert}';
		ssl_certificate_key '${path_cert_key}';
		# Ограничения на api методы, в т.ч. для работы с ВАТС
		location /api {
			# Далее идут ip адреса, имеющие доступ к api
			allow 127.0.0.1;
			# Ростелеком
		    # IP сервера Ростелеком меняется, поэтому включим всю подсеть
		    # Можно включить проверку подписи и тогда ограничение по IP не нужно
			allow 77.51.250.0/24;
			deny all;
			# Путь до нашего приложения
			proxy_pass http://localhost:2345/api;
			proxy_set_header X-Forwarded-For \$remote_addr;
			proxy_set_header X-Forwarded-Proto \$scheme;
			proxy_set_header X-Forwarded-Port \$server_port;
		}
		location / {
			allow all;
			 # Путь до нашего приложения
			proxy_pass http://localhost:2345/;
			proxy_set_header X-Forwarded-For \$remote_addr;
			proxy_set_header X-Forwarded-Proto \$scheme;
			proxy_set_header X-Forwarded-Port \$server_port;
		}
	}
EOF
	# END Запись в конфиг
	nginx -t
	systemctl restart nginx.service
}

function change_app_config(){
	# Ввод директории для логов log_dir_path
	answer=0	
	log_dir_path="${cur_dir}/log"
	while true; do
	read -p "Использовать директорию для логов приложения по-умолчанию[${log_dir_path}] (yn): " yn
    case $yn in
        [Yy]* ) answer=1;break;;
        [Nn]* ) answer=0;break;;
        * ) echo "Введите y или n";;
    esac
	done

	if [[ answer -eq 0 ]]; then	
		read -p "Введите полный путь для директории с логами: " log_dir_path
		while [ -d $log_dir_path ]
		do
			echo "Такой директории не существует. Попробуйте ещё раз"
			read -p "Введите полный путь для директории с логами: " log_dir_path
		done
	fi
	
	# Перезапись конфига приложения
	mkdir -p  BOOT-INF/classes
	prop_path="${cur_dir}/BOOT-INF/classes/application.properties"
	touch $prop_path
	cat > $prop_path <<EOF
server.port=2345
spring.datasource.url=jdbc:mysql://localhost:3306/${dbname}?useUnicode=yes&characterEncoding=UTF-8
spring.datasource.username=${dbuser}
spring.datasource.password=${dbpass}

spring.jpa.generate-ddl=true
spring.jpa.show-sql=true

logging.file.name=${log_dir_path}/application.log
logging.pattern.console=%msg%n
logging.level.root=warn

logging.level.org.springframework.web=TRACE
EOF
	jar uf app.jar -C ${cur_dir} /BOOT-INF/classes/application.properties
	rm -rv BOOT-INF
	# На всякий случай
	chmod 777 app.jar
}

function unit_create(){
	unit_path="/etc/systemd/system/application.service"
	touch ${unit_path}
	cat > $unit_path <<EOF
[Unit]
Description=Integration API Application for VATS Rostelecom
After=syslog.target
After=network.target
[Service]
User=${whoami}
Type=simple

WorkingDirectory=${cur_dir}
ExecStart=/usr/bin/java -jar app.jar
Restart=always
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=VATSapplication

[Install]
WantedBy=multi-user.target
EOF
	systemctl enable application.service
}

if ! [ $(id -u) = 0 ]; then
	echo "Запустите скрипт от root" >&2
	exit 1
fi
# Проверка наличия файла с приложением, если нет, будет предложено ввести адрес откуда скачать
if ! [ -w app.jar ]; then
	echo "Файл с приложением app.jar не найден в этой директории или нет прав доступа" >&2
	read -p "Введите ссылку на скачивание приложения или завершите скрипт и добавьте приложение в текущую директорию, назвав app.jar: " app_path
	while [ -z $app_path ]
	do
		echo "Имя не может быть пустым. Попробуйте ещё раз"
		read -p "Введите ссылку на скачивание приложения или завершите скрипт и добавьте приложение в текущую директорию, назвав app.jar: " app_path
	done
	wget -O app.jar ${app_path}
	if ! [ -w app.jar ]; then
		chmod 777 app.jar
	fi
	exit 1
fi
apt-get update
# Пакеты для установки mysql-apt-config.deb
apt install wget lsb-release gnupg -y 
wget -O mysql-apt-config.deb https://dev.mysql.com/get/mysql-apt-config_0.8.15-1_all.deb
dpkg -i mysql-apt-config.deb
rm mysql-apt-config.deb
# Добавились репо для mysql, установим все нужные зависимости
apt-get update && apt-get upgrade -y
apt-get install mysql-server openjdk-11-jdk nginx systemd -y
cur_dir=$(pwd)

# Настройка mysql
while true; do
	read -p "Вы хотите создать пользователя mysql и БД для приложениия и изменить конфигурацию приложения app.jar? [y/n]: " yn
	case $yn in
		[Yy]* ) mysql-settings;change_app_config;break;;
		[Nn]* ) break;;
		* ) echo "Введите y или n";;
	esac
done

# Настройка mysql
while true; do
	read -p "Вы хотите настроить конфигурацию nginx? [y/n]: " yn
	case $yn in
		[Yy]* ) nginx-settings;break;;
		[Nn]* ) break;;
		* ) echo "Введите y или n";;
	esac
done
# Настройка unit
while true; do
	read -p "Вы хотите создать service для управления приложением через systemctl? [y/n]: " yn
	case $yn in
		[Yy]* ) unit_create;is_unit_created = true;break;;
		[Nn]* ) is_unit_created = false;break;;
		* ) echo "Введите y или n";;
	esac
done
# Настройка unit
while true; do
	read -p "Запустить приложение? [y/n]: " yn
	case $yn in
		[Yy]* ) is_need_launch = true;break;;
		[Nn]* ) is_need_launch = false;break;;
		* ) echo "Введите y или n";;
	esac
done
if [[ $is_need_launch]]; then
	if [[ $is_unit_created = true ]]; then
		systemctl start application.service
		echo "Приложение запущено! Посмотреть статус командой 'systemctl status application.service'"
	else
		nohup java -jar app.jar
		echo "Приложение запущено!"
	fi
	echo "Данные для входа admin:admin"
fi

	