// API endpoint for the factorial application
const API_URL = 'http://localhost:8089/api/factorial';

// Get DOM elements
const stringInput = document.getElementById('stringInput');
const submitButton = document.getElementById('submitButton');
const cancelButton = document.getElementById('cancelButton');
const clearButton = document.getElementById('clearButton');
const editButtons = document.getElementById('editButtons');
const stringList = document.getElementById('stringList');
const loadingMessage = document.getElementById('loadingMessage');
const errorBox = document.getElementById('errorBox');
const errorMessage = document.getElementById('errorMessage');

let editingItemId = null; // To store the ID of the item being edited

// --- Event Listeners ---

// Initial load of factorial results
document.addEventListener('DOMContentLoaded', fetchFactorialResults);

// Handle form submission (add or update)
submitButton.addEventListener('click', handleFormSubmit);

// Handle Cancel Edit button click
cancelButton.addEventListener('click', () => {
    resetForm();
});

// Handle Clear Input button click
clearButton.addEventListener('click', () => {
    stringInput.value = '';
});

// --- API Calls ---

/**
 * Fetches and displays all factorial results from the backend.
 */
async function fetchFactorialResults() {
    loadingMessage.classList.remove('hidden');
    stringList.innerHTML = '';
    hideError();

    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const results = await response.json();
        renderFactorialResults(results);
    } catch (error) {
        showError('Failed to fetch factorial results. Please check the server connection.');
        console.error('Error fetching data:', error);
    } finally {
        loadingMessage.classList.add('hidden');
    }
}

/**
 * Adds a new factorial number to the backend.
 * @param {number} input The number to calculate the factorial for.
 */
async function addFactorialResult(input) {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ input: parseInt(input) }),
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP error! status: ${response.status}`);
        }
        await fetchFactorialResults(); // Refresh the list
        resetForm();
    } catch (error) {
        showError(`Failed to add number: ${error.message}`);
        console.error('Error adding data:', error);
    }
}

/**
 * Updates an existing factorial number in the backend.
 * @param {number} id The ID of the item to update.
 * @param {number} input The new number.
 */
async function updateFactorialResult(id, input) {
    try {
        const response = await fetch(API_URL, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id: id, input: parseInt(input) }),
        });
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP error! status: ${response.status}`);
        }
        await fetchFactorialResults(); // Refresh the list
        resetForm();
    } catch (error) {
        showError(`Failed to update number: ${error.message}`);
        console.error('Error updating data:', error);
    }
}

/**
 * Deletes a factorial result from the backend.
 * @param {number} id The ID of the item to delete.
 */
async function deleteFactorialResult(id) {
    try {
        const response = await fetch(API_URL, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ id: id }),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        await fetchFactorialResults(); // Refresh the list
    } catch (error) {
        showError('Failed to delete number. Please try again.');
        console.error('Error deleting data:', error);
    }
}

// --- UI Logic and Handlers ---

/**
 * Handles the form submission for both adding and updating.
 */
function handleFormSubmit() {
    const inputValue = stringInput.value.trim();
    if (!inputValue) {
        showError('Please enter a number.');
        return;
    }
    if (isNaN(inputValue)) {
        showError('Invalid input. Please enter a valid number.');
        return;
    }

    // Check if we are in "editing" mode or "adding" mode
    if (editingItemId) {
        updateFactorialResult(editingItemId, inputValue);
    } else {
        addFactorialResult(inputValue);
    }
}

/**
 * Renders the list of factorial results on the page.
 * @param {Array<Object>} results The array of factorial result objects.
 */
function renderFactorialResults(results) {
    stringList.innerHTML = '';
    if (results.length === 0) {
        stringList.innerHTML = '<p class="text-center text-gray-500 italic">No factorial numbers saved yet. Add one above!</p>';
        return;
    }
    results.forEach(item => {
        const itemDiv = document.createElement('div');
        itemDiv.className = 'p-4 bg-white rounded-xl shadow-sm flex flex-col md:flex-row justify-between items-center space-y-2 md:space-y-0';
        itemDiv.innerHTML = `
            <div class="flex-1">
                <p class="text-xs text-gray-400">ID: ${item.id}</p>
                <p class="text-lg font-medium text-gray-900">Input: <span class="text-blue-600">${item.factorialInput}</span></p>
                <p class="text-sm text-gray-500">Result: <span class="font-mono text-gray-800">${item.factorialResult}</span></p>
            </div>
            <div class="flex space-x-2">
                <button onclick="editItem(${item.id}, '${item.factorialInput}')" class="px-4 py-2 text-sm bg-yellow-500 text-white font-semibold rounded-lg hover:bg-yellow-600 transition-colors">Edit</button>
                <button onclick="deleteItem(${item.id})" class="px-4 py-2 text-sm bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 transition-colors">Delete</button>
            </div>
        `;
        stringList.appendChild(itemDiv);
    });
}

/**
 * Sets the form to "edit" mode.
 * @param {number} id The ID of the item to edit.
 * @param {string} input The input value of the item to edit.
 */
function editItem(id, input) {
    stringInput.value = input;
    editingItemId = id;
    submitButton.textContent = 'Update';
    editButtons.classList.remove('hidden');
    stringInput.focus();
}

/**
 * Deletes a specific item by its ID.
 * @param {number} id The ID of the item to delete.
 */
function deleteItem(id) {
    if (confirm('Are you sure you want to delete this record?')) {
        deleteFactorialResult(id);
    }
}

/**
 * Resets the form to its initial state.
 */
function resetForm() {
    stringInput.value = '';
    submitButton.textContent = 'Submit';
    editingItemId = null;
    editButtons.classList.add('hidden');
    hideError();
}

/**
 * Displays an error message.
 * @param {string} message The error message to display.
 */
function showError(message) {
    errorBox.classList.remove('hidden');
    errorMessage.textContent = message;
}

/**
 * Hides the error message.
 */
function hideError() {
    errorBox.classList.add('hidden');
    errorMessage.textContent = '';
}