$(document).ready(function () {
    var a = $("#datatable-buttons").DataTable({
        lengthChange: !1,
        // buttons: ["copy","excel","csv","pdf","print"],
        filter:false,
        language: {paginate: {previous: "<i class='mdi mdi-chevron-left'>", next: "<i class='mdi mdi-chevron-right'>"}},
        drawCallback: function () {
            $(".dataTables_paginate > .pagination").addClass("pagination-rounded")
        }
    });
});