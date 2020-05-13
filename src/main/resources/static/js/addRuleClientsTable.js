'use strict'

let $table = $('#table');
let $forAll = $('#forAll');
let $deleteDialog = $('#askDeleteDialog');
let goodMessageModal = $('#goodMessageModal');
let badMessageModal = $('#badMessageModal');

const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
const clientModal = $('#addClientDialog');

$(document).ready(function () {
    $('#checkSmartRout').click(function () {
        const manager = $('#manager');
        if ($(this).is(':checked')) {
            manager.hide(100);
        } else {
            manager.show(100);
        }
    });

    function onEditClick(value, row, index) {
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
            $table.bootstrapTable('remove', {
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

    $forAll.change(function () {
        if ($forAll.prop('checked')) {
            $('#addClient').hide();
            $table.hide();
        } else {
            $('#addClient').show();
            $table.show();
        }
    })

    $('#saveClient').click(function () {
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
            //Нужно сгенерить id
            if (!fail) {
                $.ajax({
                    type: "POST",
                    headers: {
                        'Accept': 'text/plain',
                        'Content-Type': 'application/json'
                    },
                    data: JSON.stringify({
                        number: number.val(),
                        name: name.val(),
                        id: id.val()
                    }),
                    url: "/client",
                    beforeSend: function (xhr) {
                        // here it is
                        xhr.setRequestHeader(header, token);
                    },
                    success: function (data) {
                        const client = JSON.parse(data);
                        console.log(client);
                        $table.bootstrapTable('append', [{
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
        const id = $('#id').val();
        if (id !== undefined && id !== '' && id !== '0')
            $.ajax({
                type: "GET",
                headers: {
                    'Accept': 'application/json'
                },
                url: "/rule/" + id + "/clients",
                beforeSend: function (xhr) {
                    // here it is
                    xhr.setRequestHeader(header, token);
                },
                success: function (data) {
                    console.log(data);
                    $table.bootstrapTable('load', data);
                }
                ,
                error: function (data) {
                    console.log(data);
                }
            });

        $table.bootstrapTable({
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
    $table.on('all.bs.table', function (e, name, args) {
        console.log(name, args)
    })
})