// Используются таблицы bootstrap-table.com

$(document).ready(function () {
    let $table = $('#LogsTable')

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
            //Смена пароля
            //Тратата
            const token = $("meta[name='_csrf']").attr("content");
            const header = $("meta[name='_csrf_header']").attr("content");

            $.ajax({
                type: "DELETE",
                headers: {
                    'Accept': 'text/plain',
                    'Content-Type': 'application/json'
                },
                data : JSON.stringify(ids),
                url: "/log",
                beforeSend: function(xhr) {
                    // here it is
                    xhr.setRequestHeader(header, token);
                },
                success: function( data ) {
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

    $(function () {
        $table.bootstrapTable('destroy').bootstrapTable({
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
                field: 'session_id',
                title: 'Сессия',
                sortable: true,
                align: 'center',
                filterControl: 'input'
            }, {
                field: 'timestamp',
                title: 'Время звонка',
                sortable: true,
                align: 'center',
                filterControl: 'input'
            }, {
                field: 'type',
                title: 'Тип',
                sortable: true,
                align: 'center',
                filterControl: 'select'
            }, {
                field: 'from_number',
                title: 'От',
                sortable: true,
                align: 'center',
                filterControl: 'input'
            }, {
                field: 'request_number',
                title: 'Куда',
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
                    'click .remove': function (e, value, row, index) {
                        deleteLogByIds([row.id])
                    }
                },
                formatter: [
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