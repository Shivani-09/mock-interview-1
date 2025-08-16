const BASE_URL = 'http://localhost:8089';
const reverseString_api = '/api/reverse';

// --- DOM Elements for String Reverser ---
const stringInput = document.getElementById('stringInput');
const submitButton = document.getElementById('submitButton');
const editButtonsDiv = document.getElementById('editButtons');
const cancelButton = document.getElementById('cancelButton');
const clearButton = document.getElementById('clearButton');
const stringList = document.getElementById('stringList');
const loadingMessage = document.getElementById('loadingMessage');
const errorBox = document.getElementById('errorBox');
const errorMessage = document.getElementById('errorMessage');

let isEditing = false;
let currentStringId = null;

// Function to display error messages in a custom modal
function displayError(message) {
    const modal = document.createElement('div');
    modal.className = 'fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full flex justify-center items-center';
    modal.innerHTML = `
        <div class="bg-white p-6 rounded-lg shadow-xl max-w-sm mx-auto">
            <h3 class="text-lg font-bold text-red-700">Error</h3>
            <p class="mt-2 text-gray-600">${message}</p>
            <div class="mt-4 flex justify-end">
                <button onclick="this.closest('.fixed').remove()" class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500">
                    Close
                </button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
}

// Function to clear any displayed error messages
function clearError() {
    const existingModal = document.querySelector('.fixed');
    if (existingModal) {
        existingModal.remove();
    }
}

// Function to fetch all reversed strings from the backend and render them
async function fetchStrings() {
    clearError();
    loadingMessage.classList.remove('hidden');
    try {
        const response = await fetch(`${BASE_URL}${reverseString_api}`);
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const strings = await response.json();
        renderStrings(strings);
    } catch (error) {
        console.error('Error fetching strings:', error);
        displayError('Failed to load strings. Please ensure your backend is running.');
        stringList.innerHTML = '';
    } finally {
        loadingMessage.classList.add('hidden');
    }
}

// Function to render the list of reversed strings in the UI
function renderStrings(strings) {
    stringList.innerHTML = '';
    if (strings.length === 0) {
        stringList.innerHTML = `<p class="text-center text-gray-500 italic">No strings found. Add one above!</p>`;
        return;
    }

    strings.forEach(str => {
        const itemDiv = document.createElement('div');
        itemDiv.className = 'flex flex-col md:flex-row items-center justify-between p-4 bg-white rounded-xl shadow-sm border border-gray-200';
        itemDiv.innerHTML = `
            <div class="flex-1 grid grid-cols-1 md:grid-cols-3 gap-4 md:gap-6 mb-4 md:mb-0">
                <div class="flex flex-col">
                    <p class="text-sm text-gray-500">Id:</p>
                    <p class="font-medium text-gray-800 break-all">${str.id}</p>
                </div>
                <div class="flex flex-col">
                    <p class="text-sm text-gray-500">Original:</p>
                    <p class="font-medium text-gray-800 break-all">${str.originalString}</p>
                </div>
                <div class="flex flex-col">
                    <p class="text-sm text-gray-500">Reversed:</p>
                    <p class="font-medium text-gray-800 break-all">${str.reverseString}</p>
                </div>
            </div>
            <div class="flex space-x-2">
                <button onclick="handleEdit(${str.id}, '${str.originalString.replace(/'/g, "\\'")}')" class="px-3 py-1 text-sm bg-yellow-400 text-yellow-900 rounded-xl hover:bg-yellow-500 transition-colors">
                    Edit
                </button>
                <button onclick="handleDelete(${str.id})" class="px-3 py-1 text-sm bg-red-400 text-red-900 rounded-xl hover:bg-red-500 transition-colors">
                    Delete
                </button>
            </div>
        `;
        stringList.appendChild(itemDiv);
    });
}

// Function to handle adding a new reversed string
async function addString(input) {
    try {
        const response = await fetch(`${BASE_URL}${reverseString_api}/add`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ inputDto: input })
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        await response.json();
        stringInput.value = '';
        await fetchStrings();
    } catch (error) {
        console.error('Error adding string:', error);
        displayError('Failed to add string. Check your network and backend logs.');
    }
}

// Function to handle updating an existing reversed string
async function updateString(id, input) {
    try {
        const response = await fetch(`${BASE_URL}/api/reverse/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ inputDto: input })
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        await response.json();
        resetForm();
        fetchStrings();
    } catch (error) {
        console.error('Error updating string:', error);
        displayError('Failed to update string. Check your network and backend logs.');
    }
}

// Function to handle deleting a reversed string
async function handleDelete(id) {
    // Custom modal for confirmation
    const modal = document.createElement('div');
    modal.className = 'fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full flex justify-center items-center';
    modal.innerHTML = `
        <div class="bg-white p-6 rounded-lg shadow-xl max-w-sm mx-auto">
            <h3 class="text-lg font-bold text-gray-800">Confirm Deletion</h3>
            <p class="mt-2 text-gray-600">Are you sure you want to delete this string?</p>
            <div class="mt-4 flex justify-end space-x-2">
                <button id="cancelDeleteBtn" class="px-4 py-2 bg-gray-300 text-gray-800 rounded-md hover:bg-gray-400 focus:outline-none">
                    Cancel
                </button>
                <button id="confirmDeleteBtn" class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500">
                    Delete
                </button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);

    const confirmBtn = document.getElementById('confirmDeleteBtn');
    const cancelBtn = document.getElementById('cancelDeleteBtn');

    confirmBtn.addEventListener('click', async () => {
        modal.remove(); // Close modal
        try {
            const response = await fetch(`${BASE_URL}${reverseString_api}/${id}`, {
                method: 'DELETE'
            });
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            fetchStrings();
        } catch (error) {
            console.error('Error deleting string:', error);
            displayError('Failed to delete string. Check your network and backend logs.');
        }
    });

    cancelBtn.addEventListener('click', () => {
        modal.remove(); // Close modal
    });
}

// Function to switch to edit mode for string reverser
function handleEdit(id, originalString) {
    isEditing = true;
    currentStringId = id;
    stringInput.value = originalString;
    submitButton.textContent = 'Update String';
    submitButton.classList.remove('bg-blue-600');
    submitButton.classList.add('bg-green-600');
    editButtonsDiv.classList.remove('hidden');
}

// Function to reset the form back to 'add' mode for string reverser
function resetForm() {
    isEditing = false;
    currentStringId = null;
    stringInput.value = '';
    submitButton.textContent = 'Add String';
    submitButton.classList.remove('bg-green-600');
    submitButton.classList.add('bg-blue-600');
    editButtonsDiv.classList.add('hidden');
    clearError();
}

// Event listener for the main submit button
submitButton.addEventListener('click', () => {
    const inputValue = stringInput.value.trim();
    if (inputValue === '') {
        displayError('Please enter a string before submitting.');
        return;
    }
    if (isEditing) {
        updateString(currentStringId, inputValue);
    } else {
        addString(inputValue);
    }
});

// Event listener for the cancel button (in edit mode)
cancelButton.addEventListener('click', resetForm);

// Event listener for the clear button
clearButton.addEventListener('click', () => {
    stringInput.value = '';
});

// Fetch strings on page load
document.addEventListener('DOMContentLoaded', fetchStrings);
