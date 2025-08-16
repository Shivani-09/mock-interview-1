const BASE_URL = 'http://localhost:8089';
const palindrome_api = '/api/palindrome';


// --- DOM Elements for Palindrome Checker ---
const palindromeInput = document.getElementById('palindromeInput');
const addPalindromeButton = document.getElementById('addPalindromeButton');
const editPalindromeButtonsDiv = document.getElementById('editPalindromeButtons');
const cancelPalindromeButton = document.getElementById('cancelPalindromeButton');
const clearPalindromeButton = document.getElementById('clearPalindromeButton');
const palindromeList = document.getElementById('palindromeList');
const loadingPalindromeMessage = document.getElementById('loadingPalindromeMessage');
const errorBox = document.getElementById('errorBox');
const errorMessage = document.getElementById('errorMessage');

let isEditingPalindrome = false;
let currentPalindromeId = null;

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

// Function to fetch all palindromes from the backend and render them
async function fetchPalindromes() {
    clearError();
    loadingPalindromeMessage.classList.remove('hidden');
    try {
        const response = await fetch(`${BASE_URL}${palindrome_api}`);
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const palindromes = await response.json();
        renderPalindromes(palindromes);
    } catch (error) {
        console.error('Error fetching palindromes:', error);
        displayError('Failed to load palindromes. Please ensure your backend is running.');
        palindromeList.innerHTML = '';
    } finally {
        loadingPalindromeMessage.classList.add('hidden');
    }
}

// Function to render the list of palindromes in the UI
function renderPalindromes(palindromes) {
    palindromeList.innerHTML = '';
    if (palindromes.length === 0) {
        palindromeList.innerHTML = `<p class="text-center text-gray-500 italic">No palindromes found. Check one above!</p>`;
        return;
    }

    palindromes.forEach(p => {
        const itemDiv = document.createElement('div');
        itemDiv.className = 'flex flex-col md:flex-row items-center justify-between p-4 bg-white rounded-xl shadow-sm border border-gray-200';
        itemDiv.innerHTML = `
            <div class="flex-1 grid grid-cols-1 md:grid-cols-4 gap-4 md:gap-6 mb-4 md:mb-0">
                <div class="flex flex-col">
                    <p class="text-sm text-gray-500">Id:</p>
                    <p class="font-medium text-gray-800 break-all">${p.id}</p>
                </div>
                <div class="flex flex-col">
                    <p class="text-sm text-gray-500">Original:</p>
                    <p class="font-medium text-gray-800 break-all">${p.originalString}</p>
                </div>
                <div class="flex flex-col">
                    <p class="text-sm text-gray-500">Reversed:</p>
                    <p class="font-medium text-gray-800 break-all">${p.reverseString}</p>
                </div>
                <div class="flex flex-col">
                    <p class="text-sm text-gray-500">Is Palindrome?</p>
                    <p class="font-bold ${p.palindrome ? 'text-green-600' : 'text-red-600'}">${p.palindrome}</p>
                </div>
            </div>
            <div class="flex space-x-2">
                <button onclick="handlePalindromeEdit(${p.id}, '${p.originalString.replace(/'/g, "\\'")}')" class="px-3 py-1 text-sm bg-yellow-400 text-yellow-900 rounded-xl hover:bg-yellow-500 transition-colors">
                    Edit
                </button>
                <button onclick="handlePalindromeDelete(${p.id})" class="px-3 py-1 text-sm bg-red-400 text-red-900 rounded-xl hover:bg-red-500 transition-colors">
                    Delete
                </button>
            </div>
        `;
        palindromeList.appendChild(itemDiv);
    });
}

// Function to handle adding a new palindrome string
async function addPalindrome(input) {
    try {
        const response = await fetch(`${BASE_URL}${palindrome_api}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ inputDto: input })
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        await response.json();
        palindromeInput.value = '';
        await fetchPalindromes();
    } catch (error) {
        console.error('Error adding palindrome:', error);
        displayError('Failed to add palindrome. Check your network and backend logs.');
    }
}

// Function to handle updating an existing palindrome string
async function updatePalindrome(id, input) {
    try {
        const response = await fetch(`${BASE_URL}${palindrome_api}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ inputDto: input })
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        await response.text();
        resetPalindromeForm();
        fetchPalindromes();
    } catch (error) {
        console.error('Error updating palindrome:', error);
        displayError('Failed to update palindrome. Check your network and backend logs.');
    }
}

// Function to handle deleting a palindrome string
async function handlePalindromeDelete(id) {
    // Custom modal for confirmation
    const modal = document.createElement('div');
    modal.className = 'fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full flex justify-center items-center';
    modal.innerHTML = `
        <div class="bg-white p-6 rounded-lg shadow-xl max-w-sm mx-auto">
            <h3 class="text-lg font-bold text-gray-800">Confirm Deletion</h3>
            <p class="mt-2 text-gray-600">Are you sure you want to delete this palindrome?</p>
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
        modal.remove();
        try {
            const response = await fetch(`${BASE_URL}${palindrome_api}`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id })
            });
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            await response.text();
            fetchPalindromes();
        } catch (error) {
            console.error('Error deleting palindrome:', error);
            displayError('Failed to delete palindrome. Check your network and backend logs.');
        }
    });

    cancelBtn.addEventListener('click', () => {
        modal.remove();
    });
}

// Function to switch to edit mode for palindrome checker
function handlePalindromeEdit(id, originalString) {
    isEditingPalindrome = true;
    currentPalindromeId = id;
    palindromeInput.value = originalString;
    addPalindromeButton.textContent = 'Update Palindrome';
    addPalindromeButton.classList.remove('bg-purple-600');
    addPalindromeButton.classList.add('bg-green-600');
    editPalindromeButtonsDiv.classList.remove('hidden');
}

// Function to reset the form back to 'add' mode for palindrome checker
function resetPalindromeForm() {
    isEditingPalindrome = false;
    currentPalindromeId = null;
    palindromeInput.value = '';
    addPalindromeButton.textContent = 'Add & Check Palindrome';
    addPalindromeButton.classList.remove('bg-green-600');
    addPalindromeButton.classList.add('bg-purple-600');
    editPalindromeButtonsDiv.classList.add('hidden');
    clearError();
}

// Event listener for the palindrome submit button
addPalindromeButton.addEventListener('click', () => {
    const inputValue = palindromeInput.value.trim();
    if (inputValue === '') {
        displayError('Please enter a string before submitting.');
        return;
    }
    if (isEditingPalindrome) {
        updatePalindrome(currentPalindromeId, inputValue);
    } else {
        addPalindrome(inputValue);
    }
});

// Event listener for the cancel button (in edit mode)
cancelPalindromeButton.addEventListener('click', resetPalindromeForm);

// Event listener for the clear button
clearPalindromeButton.addEventListener('click', () => {
    palindromeInput.value = '';
});

// Fetch palindromes when the page loads
document.addEventListener('DOMContentLoaded', fetchPalindromes);
