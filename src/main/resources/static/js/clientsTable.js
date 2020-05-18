'use strict'

// Используются таблицы bootstrap-table.com
/**
 *  Скрипты для страницы /clients (Список клиентов) и /client (форма редактирования клиента)
 *  Таблица со всеми клиентами
 *  Таблица с правилами для клиента
 *  Мб что то ещё
 * */

let $allClientsTable = $('#clientsTable')
let $rulesForClient = $('#rulesTable')

let goodMessage = $('#goodMessage');

let badMessage = $('#badMessage');
const idClient = $('#id').val();

const $rulesBlockForClient = $('#rules');


/**
 * Добавляем инфу о правилах перед отправкой формы
 * */
let $clientForm = $('#clientForm');
$clientForm.submit(function (event) {
    const rules = $rulesForClient.bootstrapTable('getData');
    $.each(rules, function (i, param) {
        $('<input />').attr('type', 'hidden')
            .attr('name', 'rules')
            .attr('value', param.id)
            .appendTo($clientForm);
    });
    return true;
})

$(document).ready(function () {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    var $remove = $('#remove')
    let $deleteDialog = $('#askDeleteDialog')
    var selections = []

    // Получить строки с галочкой
    function getIdSelections() {
        return $.map($allClientsTable.bootstrapTable('getSelections'), function (row) {
            return row.id
        })
    }

    // Удаление по массиву айдишников
    function deleteClientsByIds(ids) {
        //Показали окно
        $deleteDialog.modal('show');
        $('#yesDelete').click(function () {
            //Удаление с таблицы
            $allClientsTable.bootstrapTable('remove', {
                "field": 'id',
                "values": ids
            });
            // Удаление с сервера
            $.ajax({
                type: "DELETE",
                headers: {
                    'Accept': 'text/html',
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify(ids),
                dataType: 'html',
                url: "/client",
                beforeSend: function (xhr) {
                    // here it is
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    $("#messages").replaceWith(data);
                }
                ,
                error: function (data) {
                    $("#messages").replaceWith(data);
                }
            });
            // Закрытие окна
            $deleteDialog.modal('hide');
        })
    }


    /**
     * Таблица со всеми клиентами
     * */
    $(function () {
        $allClientsTable.bootstrapTable('destroy').bootstrapTable({
            columns: [{
                field: 'state',
                checkbox: true,
                align: 'center',
                valign: 'middle'
            }, {
                field: 'id',
                title: 'ID',
                align: 'center',
                sortable: true,
                valign: 'middle'
            }, {
                field: 'name',
                title: 'ФИО',
                sortable: true,
                align: 'center',
                filterControl: 'input'
            }, {
                field: 'number',
                title: 'Номер телефона',
                sortable: true,
                align: 'center',
                filterControl: 'input'
            }, {
                field: 'operate',
                title: "Действия",
                align: 'center',
                valign: 'middle',
                clickToSelect: false,
                events: {
                    'click .view': function (e, value, row, index) {
                        window.location = '/client/' + row.id + '/view';
                    },
                    'click .edit': function (e, value, row, index) {
                        window.location = '/client/' + row.id;
                    },
                    'click .remove': function (e, value, row, index) {
                        deleteClientsByIds([row.id])
                    }
                },
                formatter: [
                    '<a class="view" href="javascript:void(0)" title="Посмотреть">',
                    '<i class="fa fa-eye"></i>',
                    '</a>  ',
                    '<a class="edit" href="javascript:void(0)" title="Изменить">',
                    '<i class="fas fa-edit"></i>',
                    '</a>  ',
                    '<a class="remove" href="javascript:void(0)" title="Удалить">',
                    '<i class="fa fa-trash" style = "color:red"></i>',
                    '</a>'
                ].join('')
            }
            ]
        })
        $allClientsTable.on('check.bs.table uncheck.bs.table ' +
            'check-all.bs.table uncheck-all.bs.table',
            function () {
                $remove.prop('disabled', !$allClientsTable.bootstrapTable('getSelections').length)

                // save your data, here just save the current page
                selections = getIdSelections()
                // push or splice the selections if you want to save all data selections
            })
        $remove.click(function () {
            var ids = getIdSelections()
            deleteClientsByIds(ids)
            $remove.prop('disabled', true)
        })
    })


    /**
     * Таблица с правилами для определённого клиента
     * */
    $(function () {
        $rulesForClient.bootstrapTable('destroy').bootstrapTable({
            columns: [{
                field: 'id',
                title: 'ID',
                align: 'center',
                sortable: true,
                valign: 'middle'
            }, {
                field: 'name',
                title: 'Название',
                sortable: true,
                align: 'center',
                filterControl: 'input'
            }, {
                field: 'operate',
                title: "Действия",
                align: 'center',
                valign: 'middle',
                clickToSelect: false,
                events: {
                    'click .view': function (e, value, row, index) {
                        window.location.href = '/rule/' + row.id + '/view';
                    },
                    'click .edit': function (e, value, row, index) {
                        window.location.href = '/rule/' + row.id;
                    },
                    'click .remove': function (e, value, row, index) {
                        deleteRulesByIds([row.id])
                    }
                },
                formatter: [
                    '<a class="view" href="javascript:void(0)" title="Посмотреть">',
                    '<i class="fa fa-eye"></i>',
                    '</a>  ',
                    '<a class="edit" href="javascript:void(0)" title="Перейти к редактированию">',
                    '<i class="fas fa-edit"></i>',
                    '</a>  ',
                    '<a class="remove" href="javascript:void(0)" title="Удалить клиента из правила">',
                    '<i class="fa fa-trash" style = "color:red"></i>',
                    '</a>'
                ].join('')
            }
            ]
        })
    })

    // Вывод всех правил для клиента
    if (idClient !== undefined && idClient !== '' && idClient !== '0') {
        $.ajax({
            type: "GET",
            headers: {
                'Accept': 'application/json'
            },
            url: "/client/" + idClient + "/rules",
            // обязательно нужно добавить эти заголовки, так как csrf enabled
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (data) {
                $rulesForClient.bootstrapTable('load', data);
            }
        })
    } else {
        $rulesBlockForClient.hide();
    }

    function deleteRulesByIds(ids) {
        //Показали окно
        $deleteDialog.modal('show');
        $('#yesDelete').click(function () {
            //Удаление с таблицы
            $rulesForClient.bootstrapTable('remove', {
                "field": 'id',
                "values": ids
            });
            // Закрытие окна
            $deleteDialog.modal('hide');
        })
    }
})