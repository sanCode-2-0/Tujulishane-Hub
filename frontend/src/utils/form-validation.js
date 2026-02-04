/**
 * Form Validation Utility
 * Provides comprehensive form validation for project submission forms
 */

class FormValidator {
    constructor(formId) {
        this.form = document.getElementById(formId);
        this.errors = {};
    }

    /**
     * Validate all required fields
     */
    validateRequiredFields() {
        this.errors = {};
        
        // Get all required inputs
        const requiredFields = this.form.querySelectorAll('[required]');
        
        requiredFields.forEach(field => {
            const value = field.value.trim();
            const fieldName = field.name || field.id;
            
            if (!value) {
                this.errors[fieldName] = `${this.getFieldLabel(field)} is required`;
                this.showFieldError(field, this.errors[fieldName]);
            } else {
                this.clearFieldError(field);
            }
        });
        
        return Object.keys(this.errors).length === 0;
    }

    /**
     * Validate specific field types
     */
    validateField(field) {
        const fieldType = field.type;
        const value = field.value.trim();
        const fieldName = field.name || field.id;
        
        // Clear previous error
        this.clearFieldError(field);
        
        // Check if required
        if (field.hasAttribute('required') && !value) {
            const error = `${this.getFieldLabel(field)} is required`;
            this.errors[fieldName] = error;
            this.showFieldError(field, error);
            return false;
        }
        
        // Type-specific validation
        switch (fieldType) {
            case 'email':
                if (value && !this.isValidEmail(value)) {
                    const error = 'Please enter a valid email address';
                    this.errors[fieldName] = error;
                    this.showFieldError(field, error);
                    return false;
                }
                break;
                
            case 'number':
                if (value && isNaN(value)) {
                    const error = 'Please enter a valid number';
                    this.errors[fieldName] = error;
                    this.showFieldError(field, error);
                    return false;
                }
                
                // Check min/max
                if (field.hasAttribute('min') && parseFloat(value) < parseFloat(field.min)) {
                    const error = `Value must be at least ${field.min}`;
                    this.errors[fieldName] = error;
                    this.showFieldError(field, error);
                    return false;
                }
                
                if (field.hasAttribute('max') && parseFloat(value) > parseFloat(field.max)) {
                    const error = `Value must be at most ${field.max}`;
                    this.errors[fieldName] = error;
                    this.showFieldError(field, error);
                    return false;
                }
                break;
                
            case 'date':
                if (value && !this.isValidDate(value)) {
                    const error = 'Please enter a valid date';
                    this.errors[fieldName] = error;
                    this.showFieldError(field, error);
                    return false;
                }
                break;
                
            case 'text':
            case 'textarea':
                // Check minlength/maxlength
                if (field.hasAttribute('minlength') && value.length < parseInt(field.minlength)) {
                    const error = `Minimum length is ${field.minlength} characters`;
                    this.errors[fieldName] = error;
                    this.showFieldError(field, error);
                    return false;
                }
                
                if (field.hasAttribute('maxlength') && value.length > parseInt(field.maxlength)) {
                    const error = `Maximum length is ${field.maxlength} characters`;
                    this.errors[fieldName] = error;
                    this.showFieldError(field, error);
                    return false;
                }
                break;
        }
        
        delete this.errors[fieldName];
        return true;
    }

    /**
     * Validate themes selection (at least one required)
     */
    validateThemes() {
        const themeCheckboxes = this.form.querySelectorAll('input[name="themes"]:checked');
        const errorElement = document.getElementById('themeValidationError');
        
        if (themeCheckboxes.length === 0) {
            this.errors.themes = 'Please select at least one project theme';
            if (errorElement) {
                errorElement.classList.remove('hidden');
            }
            return false;
        }
        
        if (errorElement) {
            errorElement.classList.add('hidden');
        }
        delete this.errors.themes;
        return true;
    }

    /**
     * Validate locations (at least one required)
     */
    validateLocations() {
        const locationsInput = document.getElementById('project_locations');
        let locations = [];
        
        try {
            locations = JSON.parse(locationsInput?.value || '[]');
        } catch (e) {
            console.error('Error parsing locations:', e);
        }
        
        if (locations.length === 0) {
            this.errors.locations = 'Please select at least one location on the map';
            
            // Show visual feedback
            const mapContainer = document.getElementById('newProjectMap');
            if (mapContainer) {
                mapContainer.style.borderColor = '#ef4444';
                mapContainer.style.borderWidth = '2px';
            }
            
            return false;
        }
        
        // Clear error
        const mapContainer = document.getElementById('newProjectMap');
        if (mapContainer) {
            mapContainer.style.borderColor = '#e5e7eb';
            mapContainer.style.borderWidth = '1px';
        }
        
        delete this.errors.locations;
        return true;
    }

    /**
     * Validate budget field
     */
    validateBudget() {
        const budgetInput = document.getElementById('budget');
        const currencySelect = document.getElementById('currency');
        
        if (!budgetInput || !currencySelect) return true;
        
        const budget = budgetInput.value.replace(/,/g, '');
        const currency = currencySelect.value;
        
        // Check currency is selected
        if (budgetInput.hasAttribute('required') && !currency) {
            this.errors.currency = 'Please select a currency';
            this.showFieldError(currencySelect, this.errors.currency);
            return false;
        }
        
        // Check budget is a positive number
        if (budgetInput.hasAttribute('required') && (!budget || isNaN(budget) || parseFloat(budget) <= 0)) {
            this.errors.budget = 'Please enter a valid budget amount greater than 0';
            this.showFieldError(budgetInput, this.errors.budget);
            return false;
        }
        
        this.clearFieldError(budgetInput);
        this.clearFieldError(currencySelect);
        delete this.errors.budget;
        delete this.errors.currency;
        return true;
    }

    /**
     * Validate date range (start date before end date)
     */
    validateDateRange() {
        const startDateInput = document.getElementById('starttime_period');
        const endDateInput = document.getElementById('endtime_period');
        
        if (!startDateInput || !endDateInput) return true;
        
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);
        
        if (endDateInput.value && startDate >= endDate) {
            this.errors.endDate = 'End date must be after start date';
            this.showFieldError(endDateInput, this.errors.endDate);
            return false;
        }
        
        this.clearFieldError(endDateInput);
        delete this.errors.endDate;
        return true;
    }

    /**
     * Show error message for a field
     */
    showFieldError(field, message) {
        // Add error styling to field
        field.classList.add('border-red-500', 'focus:border-red-500', 'focus:ring-red-500');
        field.classList.remove('border-gray-200', 'focus:border-blue-500', 'focus:ring-blue-500');
        
        // Find or create error message element
        let errorElement = field.parentElement.querySelector('.field-error-message');
        
        if (!errorElement) {
            errorElement = document.createElement('p');
            errorElement.className = 'field-error-message text-red-500 text-sm mt-1';
            field.parentElement.appendChild(errorElement);
        }
        
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }

    /**
     * Clear error message for a field
     */
    clearFieldError(field) {
        // Remove error styling
        field.classList.remove('border-red-500', 'focus:border-red-500', 'focus:ring-red-500');
        field.classList.add('border-gray-200', 'focus:border-blue-500', 'focus:ring-blue-500');
        
        // Hide error message
        const errorElement = field.parentElement.querySelector('.field-error-message');
        if (errorElement) {
            errorElement.style.display = 'none';
        }
    }

    /**
     * Get user-friendly field label
     */
    getFieldLabel(field) {
        // Try to find label element
        const label = this.form.querySelector(`label[for="${field.id}"]`);
        if (label) {
            return label.textContent.replace('*', '').trim();
        }
        
        // Fallback to field name/id
        return (field.name || field.id).replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    }

    /**
     * Email validation
     */
    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    /**
     * Date validation
     */
    isValidDate(dateString) {
        const date = new Date(dateString);
        return date instanceof Date && !isNaN(date);
    }

    /**
     * Validate entire form
     */
    validateAll() {
        const isValidFields = this.validateRequiredFields();
        const isValidThemes = this.validateThemes();
        const isValidLocations = this.validateLocations();
        const isValidBudget = this.validateBudget();
        const isValidDateRange = this.validateDateRange();
        
        return isValidFields && isValidThemes && isValidLocations && isValidBudget && isValidDateRange;
    }

    /**
     * Get all errors
     */
    getErrors() {
        return this.errors;
    }

    /**
     * Show summary of all errors
     */
    showErrorSummary() {
        const errorKeys = Object.keys(this.errors);
        
        if (errorKeys.length === 0) return;
        
        const errorList = errorKeys.map(key => `• ${this.errors[key]}`).join('\n');
        
        alert(`Please fix the following errors:\n\n${errorList}`);
    }

    /**
     * Scroll to first error
     */
    scrollToFirstError() {
        const firstErrorField = this.form.querySelector('.border-red-500');
        if (firstErrorField) {
            firstErrorField.scrollIntoView({ behavior: 'smooth', block: 'center' });
            firstErrorField.focus();
        }
    }
}

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FormValidator;
}
