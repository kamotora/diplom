'use strict'

let $clientsInRulesTable = $('#table');
let $existingClientsTable = $('#modal_table');

let $forAll = $('#forAll');
let $deleteDialog = $('#askDeleteDialog');
let goodMessageModal = $('#goodMessageModal');
let badMessageModal = $('#badMessageModal');

const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
const clientModal = $('#addClientDialog');

$(document).ready(function () {

    //Скрываем поле для ввода менеджера, если умная маршрутизация, и наоборот
    const managerBlock = $('#manager');
    const isClever = $('#checkSmartRout');
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
    if ($forAll.prop('checked'))
        $forAll.change(function () {
            if ($forAll.prop('checked')) {
                clientsBlock.hide();
            } else {
                clientsBlock.show();
            }
        })


    function onEditClick(value, row, index) {
        // Добавляем на форму значения с таблицы
        let number = clientModal.find('#number_client');
        let name = clientModal.find('#name_client');
        let id = clientModal.find('#id_client');
        number.val(row.number);
        name.val(row.name);
        id.val(row.id);
        clientModal.modal('show');
    }

    function deleteClientsByIds(ids) {
        //Показали окно
        $deleteDialog.modal('show');
        $('#yesDelete').click(function () {
            //Удаление с таблицы
            $clientsInRulesTable.bootstrapTable('remove', {
                "field": 'id',
                "values": ids
            });
            // Удаление с сервера
            $.ajax({
                type: "DELETE",
                headers: {
                    'Accept': 'text/plain, application/json',
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify(ids),
                url: "/client",
                beforeSend: function (xhr) {
                    // here it is
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    console.log(data);
                }
                ,
                error: function (data) {
                    console.log(data);
                }
            });
            // Закрытие окна
            $deleteDialog.modal('hide');
        })
    }

    $('#saveClient').click(function () {
            // Обрабатываем значения для клиента с формы
            let number = clientModal.find('#number_client');
            let name = clientModal.find('#name_client');
            let id = clientModal.find('#id_client');
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
                        id: id.val()
                    }),
                    url: "/client",
                    // обязательно нужно добавить эти заголовки, так как csrf enabled
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(header, token);
                    },
                    success: function (client) {
                        console.log(client);
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
                        console.log(data);
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
                clickToSelect: false,
                events: {
                    'click .edit': onEditClick,
                    'click .remove': function (e, value, row, index) {
                        deleteClientsByIds([row.id])
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
     * Таблица с Существующими клиентами
     * */
    $existingClientsTable.bootstrapTable({
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
            $existingClientsTable.bootstrapTable('load', data);
        }
    })
})