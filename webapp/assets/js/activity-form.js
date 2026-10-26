(function () {
    var form = document.getElementById("activityForm");
    if (!form) {
        return;
    }
    var byId = function (id) { return document.getElementById(id); };
    var deptBox = byId("departmentCombobox");
    var staffBox = byId("staffCombobox");
    var hint = byId("responsibleStaffMessage");
    var nature = byId("nature");
    var regForm = byId("registrationFormRequired");

    // Staff cascade: reload staff options whenever the unit changes.
    // comboboxApi is created by activity.js (loaded after this file), so it is read lazily.
    var version = 0;
    async function loadStaff() {
        var checked = deptBox.querySelector("input:checked");
        var api = staffBox.comboboxApi;
        var current = ++version;
        hint.textContent = "";
        api.setOptions([]);
        if (!checked) {
            api.setPlaceholder("Chọn đơn vị phụ trách trước");
            api.setDisabled(true);
            return;
        }
        api.setPlaceholder("Đang tải nhân viên...");
        api.setDisabled(true);
        var url = new URL(deptBox.dataset.staffEndpoint, document.baseURI);
        url.searchParams.set("action", "getStaffsByDepartment");
        url.searchParams.set("departmentId", checked.value);
        try {
            var res = await fetch(url.toString());
            if (!res.ok || !(res.headers.get("content-type") || "").includes("application/json")) {
                throw new Error("Không thể tải danh sách nhân sự của đơn vị.");
            }
            var staffs = await res.json();
            if (current !== version) {
                return;
            }
            api.setOptions(staffs.map(function (s) {
                return { id: s.id, name: [s.departmentCode, s.departmentName, s.code, s.name].filter(Boolean).join(" - ") };
            }));
            api.setPlaceholder("Chọn nhân viên");
            api.setDisabled(false);
        } catch (e) {
            if (current === version) {
                hint.textContent = e.message;
                api.setPlaceholder("Không có dữ liệu nhân viên");
            }
        }
    }
    deptBox.addEventListener("change", loadStaff);

    // Flatpickr: shown as dd/mm/yyyy HH:mm (24h), submitted as yyyy-MM-ddTHH:mm
    var pickers = ["startTime", "endTime"].map(function (id) {
        var input = byId(id);
        var picker = flatpickr(input, {
            locale: "vn",
            enableTime: true,
            time_24hr: true,
            minuteIncrement: 1,
            dateFormat: "Y-m-d\\TH:i",
            altInput: true,
            altFormat: "d/m/Y H:i",
            altInputClass: "form-control",
            monthSelectorType: "dropdown",
            allowInput: true,
            disableMobile: true
        });
        picker.altInput.placeholder = input.placeholder;
        picker.altInput.maxLength = 16;
        picker.altInput.inputMode = "numeric";
        // Auto-format typed digits as dd/mm/yyyy HH:mm
        picker.altInput.addEventListener("input", function (e) {
            if (e.inputType && e.inputType.indexOf("delete") === 0) { return; }
            var d = picker.altInput.value.replace(/\D/g, "").slice(0, 12);
            var out = d.slice(0, 2);
            if (d.length > 2) { out += "/" + d.slice(2, 4); }
            if (d.length > 4) { out += "/" + d.slice(4, 8); }
            if (d.length > 8) { out += " " + d.slice(8, 10); }
            if (d.length > 10) { out += ":" + d.slice(10, 12); }
            picker.altInput.value = out;
        });
        picker.altInput.addEventListener("blur", function () { validate(input); });
        input.closest(".date-input-group").querySelector(".date-input-icon").addEventListener("click", function () {
            picker.open();
        });
        return picker;
    });
    pickers[0].config.onChange.push(function (d) {
        pickers[1].set("minDate", d[0] || null);
    });
    if (pickers[0].selectedDates[0]) {
        pickers[1].set("minDate", pickers[0].selectedDates[0]);
    }
    // Only "Cần đăng ký" activities can require the registration form
    function syncNature() {
        var needsRegistration = nature.value === "REQUIRE_REGISTRATION";
        regForm.disabled = !needsRegistration;
        if (!needsRegistration) {
            regForm.checked = false;
        }
    }
    nature.addEventListener("change", syncNature);
    syncNature();

    // Inline validation
    function fieldOf(el) { return el.closest(".field"); }
    function validate(el) {
        var empty = !String(el.value || "").trim();
        var msg = fieldOf(el).querySelector(".error-msg");
        var text = empty ? "Vui lòng nhập thông tin này." : "";
        if (!empty && el.id === "endTime" && byId("startTime").value && el.value < byId("startTime").value) {
            text = "Thời gian kết thúc phải sau thời gian bắt đầu.";
        }
        msg.textContent = text;
        fieldOf(el).classList.toggle("has-error", !!text);
        return !text;
    }
    var required = Array.prototype.slice.call(form.querySelectorAll("input[data-required], select[data-required]"));
    required.forEach(function (el) {
        el.addEventListener("change", function () { validate(el); });
        el.addEventListener("blur", function () { validate(el); });
    });

    function validateCombobox(box) {
        var invalid = !box.querySelector("input:checked") && !box.querySelector(".multi-select-trigger").disabled;
        box.classList.toggle("is-invalid", invalid);
        if (invalid) {
            box.querySelector(".multi-select-count").textContent = "Vui lòng chọn " + box.dataset.itemLabel;
        }
        return !invalid;
    }

    form.addEventListener("submit", function (e) {
        var badFields = required.filter(function (el) { return !validate(el); });
        var badBoxes = [deptBox, staffBox].filter(function (box) { return !validateCombobox(box); });
        if (badFields.length || badBoxes.length) {
            e.preventDefault();
            e.stopImmediatePropagation();
            var first = badBoxes.length ? badBoxes[0].querySelector(".multi-select-trigger")
                : (badFields[0]._flatpickr ? badFields[0]._flatpickr.altInput : badFields[0]);
            first.scrollIntoView({ behavior: "smooth", block: "center" });
        }
    });
})();
