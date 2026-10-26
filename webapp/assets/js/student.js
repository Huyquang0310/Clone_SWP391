document.addEventListener("DOMContentLoaded", function () {

    // =========================
    // 1. Get elements
    // =========================

    const searchInput =
        document.getElementById("studentSearch");

    const statusFilter =
        document.getElementById("studentStatusFilter");

    const rows =
        document.querySelectorAll(".student-row");

    const emptyMessage =
        document.getElementById("studentEmpty");


    // =========================
    // 2. Filter students
    // =========================

    function filterStudents() {

        const keyword =
            searchInput
                ? searchInput.value.trim().toLowerCase()
                : "";

        const selectedStatus =
            statusFilter
                ? statusFilter.value.toLowerCase()
                : "";

        let visibleCount = 0;


        rows.forEach(function (row) {

            const rowText =
                row.textContent.toLowerCase();

            const rowStatus =
                (row.dataset.status || "")
                    .toLowerCase();


            // Search theo nội dung của row
            const matchKeyword =
                keyword === "" ||
                rowText.includes(keyword);


            // Filter theo status
            const matchStatus =
                selectedStatus === "" ||
                rowStatus === selectedStatus;


            // Row có được hiển thị không?
            const visible =
                matchKeyword &&
                matchStatus;


            row.style.display =
                visible ? "" : "none";


            if (visible) {
                visibleCount++;
            }
        });


        // Không có kết quả
        if (emptyMessage) {

            emptyMessage.style.display =
                visibleCount === 0
                    ? ""
                    : "none";
        }
    }


    // =========================
    // 3. Events
    // =========================

    if (searchInput) {

        searchInput.addEventListener(
            "input",
            filterStudents
        );
    }


    if (statusFilter) {

        statusFilter.addEventListener(
            "change",
            filterStudents
        );
    }
});