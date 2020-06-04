// Используются таблицы bootstrap-table.com
let $table = $('#LogsTable')

$(document).ready(function () {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    const $remove = $('#remove');
    let $deleteDialog = $('#askDeleteDialog');
    let $startFilter = $('#startFilter');
    let $resetFilter = $('#resetFilter');
    let $PNGloadGraphic = $('#JPEGDownload');
    let selections = [];
    let myChart;

    // Получить строки с галочкой
    function getIdSelections() {
        return $.map($table.bootstrapTable('getSelections'), function (row) {
            return row.id
        })
    }

    let ctx = null;
    if( document.getElementById('myChart'))
        ctx = document.getElementById('myChart').getContext('2d');

    //Получить графики
    function getGraphics(values){
        //Очистим данные
        if(ctx == null)
            return;
        if(myChart !== undefined)
            myChart.destroy();
         myChart = new Chart(ctx,{
            type: 'bar',
            data: {
                labels: ["Входящий","Исходящий","Внутренний"],
                datasets: [{
                    label: 'По типам звонков',
                    data: values,
                    backgroundColor:[
                        'green',
                        'blue',
                        'gray'
                    ],
                    borderColor: [
                        'green',
                        'blue',
                        'gray'
                    ],
                    borderWitdh: 2
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
                }
            }
        });

/*
        const ctxP = document.getElementById('pieChart').getContext('2d');
        const pieChart = new Chart(ctxP, {
            type: 'pie',
            data: {
                labels: ["Принятые", "Пропущенные"],
                datasets: [{
                    data: [340, 20],
                    backgroundColor: [
                        'blue',
                        'red'
                    ],
                    hoverBackgroundColor: [
                        'blue',
                        'red'
                    ]
                }]
            },
            options: {
                responsive: true
            }
        });*/
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
    $startFilter.click(function () {
        const startDate = $('#startDate').val();
        const finishDate = $('#finishDate').val();
        if(startDate === undefined || finishDate === undefined || startDate === "" || finishDate === "")
            // TODO Тут можно вывести красиво
            return false;
        $.ajax({
            type: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            data : JSON.stringify({
                startDate: startDate,
                finishDate: finishDate
            }),
            url: "/logs",
            beforeSend: function(xhr) {
                // here it is
                xhr.setRequestHeader(header, token);
            },
            success: function( data ) {
                console.log(data);
                $table.bootstrapTable('load', data);
            }
            ,
            error: function (data) {
                console.log(data);
            }
        });

        //Обновим графики
        $.ajax({
            type: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            data : JSON.stringify({
                startDate: startDate,
                finishDate: finishDate
            }),
            url:"/rest/logs/updateDataForGraphics",
            beforeSend: function(xhr) {
                // here it is
                xhr.setRequestHeader(header, token);
            },
            success: function(data){
                getGraphics(data);
            },
            error:function(data){
                console.log(data);
            }
        })
    });

    $resetFilter.click(function () {
        $table.bootstrapTable('refresh');
        $.ajax({
            type: "GET",
            async: false,
            url:"/rest/logs/dataGraphic",
            beforeSend: function(xhr) {
                // here it is
                xhr.setRequestHeader(header, token);
            },
            success: function(data){
                getGraphics(data);
            },
            error:function(data){
            }
        })
    });

    $PNGloadGraphic.click(function(){

        var canvas = document.getElementById("myChart");
        canvas.toBlob(function(blob){
            saveAs(blob, 'image.jpg');
        });
    });

    // Действия по клику на иконку "посмотреть"
    function onViewClick(value, row, index) {
        window.location = '/log/' + row.id + '/view';
    }
    /*
    function onRecordClick(value, row, index) {
        window.location = '/log/' + row.id + '/record';
    }
*/
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
                field: 'state_call',
                title: 'Состояние',
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
                    'click .view': function (e, value, row, index) {
                        onViewClick(value, row, index)
                    },
                    'click .remove': function (e, value, row, index) {
                        deleteLogByIds([row.id])
                    }
                },
                formatter: [
                    '<a class="view" href="javascript:void(0)" title="Посмотреть инфу о звонке">',
                    '<i class="fa fa-eye"></i>',
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
            deleteLogByIds(ids)
            $remove.prop('disabled', true)
        })
    })

    //Графики

    $(function () {
        //Считаем скок каждого вызова

        $.ajax({
            type: "GET",
            async: false,
            url:"/rest/logs/dataGraphic",
            success: function(data){
                getGraphics(data);
            },
            error:function(data){
            }
        })
    })
})