'use strict'

/**
 *  Скрипты для страницы /rule
 *  Таблица с клиентами для правила
 *  Таблица с существующими клиентами при добавлении клиента (модальное окно "Добавить клиента")
 *  Валидация вводимых значений для модального окна "Добавить клиента"
 *  Мб что то ещё
 * */

let $clientsInRulesTable = $('#table');
let $allClientsInModal = $('#modal_table');

let $forAll = $('#forAll');
let $deleteDialog = $('#askDeleteDialog');
let goodMessageModal = $('#goodMessageModal');
let badMessageModal = $('#badMessageModal');

const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
const clientModal = $('#addClientDialog');
let $addClientForm = $('#addNewClientForm');
/**
 * Добавляем инфу о клиентах перед отправкой формы
 * */
let $ruleForm = $('#ruleForm');
$ruleForm.submit(function (event) {
    const clients = $clientsInRulesTable.bootstrapTable('getData');
    $.each(clients, function (i, param) {
        $('<input />').attr('type', 'hidden')
            .attr('name', 'clients')
            .attr('value', param.id)
            .appendTo($ruleForm);
    });

    return true;
})

$(document).ready(function () {
    // Если кнопка скрыта - мы в режиме просмотра
    // Скроем колонку с действиями
    const isView = $('#addClient').is(':hidden')

    //Скрываем поле для ввода менеджера, если умная маршрутизация, и наоборот
    const managerBlock = $('#manager');
    const isClever = $('#isSmart');
    if (isClever.is(':checked'))
        managerBlock.hide();
    isClever.click(function () {
        if ($(this).is(':checked')) {
            managerBlock.hide(100);
        } else {
            managerBlock.show(100);
        }
    });

    //Скрываем поле для ввода клиентов, если в правиле активно "для всех клиентов", и наоборот
    const clientsBlock = $('#clients');
    $forAll.click(function () {
        if ($forAll.is(':checked')) {
            clientsBlock.hide(100);
        } else {
            clientsBlock.show(100);
        }
    })

    /**
     * Отображение формы для редактирования клиента
     * */
    function onEditClick(e, value, row, index) {
        // Добавляем на форму значения с таблицы
        let number = clientModal.find('#number_client');
        let name = clientModal.find('#name_client');
        let id = clientModal.find('#id_client');
        number.val(row.number);
        name.val(row.name);
        id.val(row.id);
        clientModal.modal('show');
    }

    /**
     * Удаление клиентов из правила
     * */
    function deleteClientsByIds(id) {
        //Показали окно
        $deleteDialog.modal('show');
        $('#yesDelete').click(function () {
            //Удаление с таблицы
            $clientsInRulesTable.bootstrapTable('remove', {
                "field": 'id',
                "values": [id]
            });
            $deleteDialog.modal('hide');
        })
    }

    /**
     * Сохранение нового клиента в таблицу и в бд
     * */
    $addClientForm.submit(function (e) {
            // Отменяем перезагрузку страницы при сабмите
            e.preventDefault()
            // Обрабатываем значения для клиента с формы
            let number = $addClientForm.find('#number_client');
            let name = $addClientForm.find('#name_client');
            let idValue = $addClientForm.find('#id_client').val();
            let fail = false;
            if (number.val() === '') {
                number.addClass('is-invalid');
                fail = true;
            } else {
                number.removeClass('is-invalid');
            }
            if (name.val() === '') {
                name.addClass('is-invalid');
                fail = true;
            } else {
                name.removeClass('is-invalid');
            }
            console.log(number.val());
            if (idValue === undefined || idValue === '' || idValue === '0')
                idValue = null;
            // Добавляем клиента в базу, а затем и в таблицу
            if (!fail) {
                $.ajax({
                    type: "POST",
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    data: JSON.stringify({
                        number: number.val(),
                        name: name.val(),
                        id: idValue
                    }),
                    url: "/api/client",
                    // обязательно нужно добавить эти заголовки, так как csrf enabled
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(header, token);
                    },
                    success: function (client) {
                        //console.log(client);
                        $clientsInRulesTable.bootstrapTable('append', [{
                            number: client.number,
                            name: client.name,
                            id: client.id
                        }]);
                        number.val('');
                        name.val('');
                        badMessageModal.hide();
                        clientModal.modal('hide');
                    }
                    ,
                    error: function (data) {
                        //console.log(data);
                        badMessageModal.text("Ошибка! " + data);
                        badMessageModal.show();
                    }
                });
            }
        }
    )


    $(function () {
        // Вывод всех клиентов в правиле (если id правила есть)
        const id = $('#id').val();
        if (id !== undefined && id !== '' && id !== '0')
            $.ajax({
                type: "GET",
                headers: {
                    'Accept': 'application/json'
                },
                url: "/rule/" + id + "/clients",
                // обязательно нужно добавить эти заголовки, так как csrf enabled
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    console.log(data);
                    $clientsInRulesTable.bootstrapTable('load', data);
                }
                ,
                error: function (data) {
                    console.log(data);
                }
            });

        /**
         * Таблица с клиентами для правила (на самой странице редактирования правила)
         * */
        $clientsInRulesTable.bootstrapTable({
            columns: [{
                field: 'id',
                title: 'ID',
                sortable: true,
                align: 'center',
                valign: 'middle',
            }, {
                field: 'number',
                title: 'Номер телефона',
                sortable: true,
                align: 'center',
            }, {
                field: 'name',
                title: 'ФИО Клиента',
                sortable: true,
                align: 'center',
            }, {
                field: 'operate',
                title: "Действия",
                align: 'center',
                valign: 'middle',
                visible: !isView,
                clickToSelect: false,
                events: {
                    'click .edit': onEditClick,
                    'click .remove': function (e, value, row, index) {
                        deleteClientsByIds(row.id)
                    }
                },
                formatter: [
                    '<a class="edit" href="javascript:void(0)" title="Изменить">',
                    '<i class="fas fa-edit"></i>',
                    '</a>  ',
                    '<a class="remove" href="javascript:void(0)" title="Удалить">',
                    '<i class="fa fa-trash"></i>',
                    '</a>'
                ].join('')
            }
            ]
        })
    })

    /**
     * Таблица с Существующими клиентами (модальное окно "Добавление клиента")
     * */
    $allClientsInModal.bootstrapTable({
        // Фильтр включён
        filterControl: true,
        columns: [{
            field: 'id',
            title: 'ID',
            sortable: true,
            align: 'center',
            valign: 'middle',
            filterControl: 'input'
        }, {
            field: 'number',
            title: 'Номер телефона',
            sortable: true,
            align: 'center',
            filterControl: 'input'
        }, {
            field: 'name',
            title: 'ФИО Клиента',
            sortable: true,
            align: 'center',
            filterControl: 'input'
        }, {
            field: 'operate',
            title: "Добавить в правило",
            align: 'center',
            valign: 'middle',
            clickToSelect: false,
            events: {
                'click .add': function (e, value, row, index) {
                    $clientsInRulesTable.bootstrapTable('append', [{
                        number: row.number,
                        name: row.name,
                        id: row.id
                    }]);
                    goodMessage.show();
                    goodMessage.text("Добавлено")
                }
            },
            formatter: [
                '<a class="add" href="javascript:void(0)" title="Добавить в правило">',
                '<i class="far fa-plus-square"></i>',
                '</a>  '
            ].join('')
        }
        ]
    })

    $.ajax({
        type: "GET",
        headers: {
            'Accept': 'application/json'
        },
        url: "/api/client/all",
        // обязательно нужно добавить эти заголовки, так как csrf enabled
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            $allClientsInModal.bootstrapTable('load', data);
        }
    })
})