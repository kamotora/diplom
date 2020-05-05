// Используются таблицы bootstrap-table.com

$(document).ready(function () {
    let $table = $('#table')

    var $remove = $('#remove')
    let $deleteDialog = $('#askDeleteDialog')
    var selections = []

    // Получить строки с галочкой
    function getIdSelections() {
        return $.map($table.bootstrapTable('getSelections'), function (row) {
            return row.id
        })
    }

    // Показать подробнее (плюсик)
    function detailFormatter(index, row) {
        let html = [];
        $.each(row, function (key, value) {
            html.push('<p><b>' + key + ':</b> ' + value + '</p>')
        })
        return html.join('');
    }

    // Действия по клику на иконку "изменить"
    function onEditClick(value, row, index) {
        window.location = '/user/' + row.id;
    }

    // Удаление по массиву айдишников
    function deleteLogByIds(ids) {
        //Показали окно
        $deleteDialog.modal('show');
        $('#yesDelete').click(function () {
            //Удаление с таблицы
            $table.bootstrapTable('remove', {
                "field": 'id',
                "values": ids
            });
            // Удаление с сервера

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
                        deleteLogByIds([row.id])
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
        $table.on('check.bs.table uncheck.bs.table ' +
            'check-all.bs.table uncheck-all.bs.table',
            function () {
                $remove.prop('disabled', !$table.bootstrapTable('getSelections').length)

                // save your data, here just save the current page
                selections = getIdSelections()
                // push or splice the selections if you want to save all data selections
            })
        $table.on('all.bs.table', function (e, name, args) {
            console.log(name, args)
        })
        $remove.click(function () {
            var ids = getIdSelections()
            deleteLogByIds(ids)
            $remove.prop('disabled', true)
        })
    })


})