// Используются таблицы bootstrap-table.com

$(document).ready(function () {
    let $table = $('#table')

    const $remove = $('#remove');
    let $deleteDialog = $('#askDeleteDialog')
    let $messages = $('#messages')
    let selections = [];

    // Получить строки с галочкой
    function getIdSelections() {
        return $.map($table.bootstrapTable('getSelections'), function (row) {
            return row.id
        })
    }
    // Действия по клику на иконку "изменить"
    function onEditClick(value, row, index) {
        window.location = '/user/' + row.id;
    }

    // Удаление по массиву айдишников
    function deleteUsersByIds(ids) {
        //Показали окно
        $deleteDialog.modal('show');
        $('#yesDelete').click(function () {
            //Удаление с таблицы
            $table.bootstrapTable('remove', {
                "field": 'id',
                "values": ids
            });
            // Удаление с сервера
            //Смена пароля
            //Тратата
            const token = $("meta[name='_csrf']").attr("content");
            const header = $("meta[name='_csrf_header']").attr("content");

            $.ajax({
                type: "DELETE",
                headers: {
                    'Accept': 'text/plain, application/json',
                    'Content-Type': 'application/json'
                },
                data : JSON.stringify(ids),
                url: "/user",
                beforeSend: function(xhr) {
                    // here it is
                    xhr.setRequestHeader(header, token);
                },
                success: function( data ) {
                    $messages.replaceWith(data);
                }
                ,
                error: function (data) {
                    $messages.replaceWith(data);
                }
            });
            // Закрытие окна
            $deleteDialog.modal('hide');
        })

    }

    $(function () {
        $table.bootstrapTable('destroy').bootstrapTable({
            // Фильтр включён
            filterControl: true,
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
                field: 'username',
                title: 'Логин',
                sortable: true,
                align: 'center',
                filterControl: 'input'
            }, {
                field: 'number',
                title: 'Номер',
                sortable: true,
                align: 'center',
                filterControl: 'input'
            }, {
                field: 'role',
                title: 'Роль',
                sortable: true,
                align: 'center',
                filterControl: 'select'
            }, {
                field: 'operate',
                title: "Действия",
                align: 'center',
                valign: 'middle',
                clickToSelect: false,
                events: {
                    'click .edit': function (e, value, row, index) {
                        onEditClick(value, row, index)
                    },
                    'click .remove': function (e, value, row, index) {
                        deleteUsersByIds([row.id])
                    }
                },
                formatter: [
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
        $table.on('check.bs.table uncheck.bs.table ' +
            'check-all.bs.table uncheck-all.bs.table',
            function () {
                $remove.prop('disabled', !$table.bootstrapTable('getSelections').length)

                // save your data, here just save the current page
                selections = getIdSelections()
                // push or splice the selections if you want to save all data selections
            })
        $remove.click(function () {
            const ids = getIdSelections();
            deleteUsersByIds(ids)
            $remove.prop('disabled', true)
        })
    })


})