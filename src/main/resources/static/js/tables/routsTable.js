// Используются таблицы bootstrap-table.com

let $table = $('#RulesTable')

$(document).ready(function () {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    const $remove = $('#remove');
    let $deleteDialog = $('#askDeleteDialog')
    let selections = [];

    // Получить строки с галочкой
    function getIdSelections() {
        return $.map($table.bootstrapTable('getSelections'), function (row) {
            return row.id
        })
    }

    // Действия по клику на иконку "изменить"
    function onEditClick(value, row, index) {
        window.location = '/rule/' + row.id;
    }

    // Действия по клику на иконку "посмотреть"
    function onViewClick(value, row, index) {
        window.location = '/rule/' + row.id + '/view';
    }

    // Удаление по массиву айдишников
    function deleteRuleByIds(ids) {
        //Показали окно
        $deleteDialog.modal('show');
        $('#yesDelete').click(function () {
            //Удаление с таблицы
            $table.bootstrapTable('remove', {
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
                data : JSON.stringify(ids),
                url: "/rule",
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function( data ) {
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
                        onViewClick(value, row, index)
                    },
                    'click .edit': function (e, value, row, index) {
                        onEditClick(value, row, index)
                    },
                    'click .remove': function (e, value, row, index) {
                        deleteRuleByIds([row.id])
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
            const ids = getIdSelections();
            deleteRuleByIds(ids)
            $remove.prop('disabled', true)
        })
    })


})