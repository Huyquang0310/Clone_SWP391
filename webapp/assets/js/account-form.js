/**
 * Filters the Role options in the create/edit forms by the selected Type
 * (each role option carries data-type). UX only: the server re-validates the
 * (type, role) pair.
 */
(function () {
    'use strict';

    function init() {
        var typeSelect = document.getElementById('type');
        var roleSelect = document.getElementById('role');
        if (!typeSelect || !roleSelect) {
            return;
        }

        function updateRoleOptions() {
            var selectedType = typeSelect.value;
            var visibleOptions = 0;
            var options = roleSelect.options;

            for (var i = 0; i < options.length; i++) {
                var option = options[i];
                var visible = !option.value || option.getAttribute('data-type') === selectedType;
                option.style.display = visible ? '' : 'none';
                if (option.selected && !visible) {
                    option.selected = false;
                }
                if (visible && option.value) {
                    visibleOptions++;
                }
            }
            roleSelect.disabled = visibleOptions === 0;
        }

        typeSelect.addEventListener('change', updateRoleOptions);
        updateRoleOptions();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();