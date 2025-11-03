document.addEventListener('DOMContentLoaded', () => {
    // 1. Select all input fields with the class 'numbers-only'
    const numberInputs = document.querySelectorAll('.numbers-only');

    // 2. Define the formatting function
    function formatNumberWithCommas(inputElement) {
        // Get the current value
        let value = inputElement.value;

        // a. Remove all non-digit characters (including existing commas)
        // This ensures we are always working with the raw number value
        let rawNumber = value.replace(/[^0-9]/g, '');

        // b. Convert the raw number to a locale-formatted string with commas
        // Using toLocaleString is a robust way to handle thousands separators
        // Note: The empty check prevents formatting an empty string to '0'
        if (rawNumber) {
            // Convert to a number for formatting, then back to a string
            let formattedNumber = Number(rawNumber).toLocaleString('en-US');

            // c. Update the input field's value
            inputElement.value = formattedNumber;
        } else {
            // Clear the value if nothing is left
            inputElement.value = '';
        }
    }

    // 3. Attach the event listener to each selected input
    numberInputs.forEach(input => {
        // Use 'input' event for real-time formatting
        input.addEventListener('input', () => {
            formatNumberWithCommas(input);
        });

        // Optionally, run it once on load to format pre-filled values
        formatNumberWithCommas(input);
    });
});