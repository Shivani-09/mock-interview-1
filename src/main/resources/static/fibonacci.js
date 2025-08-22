// fibonacci.js

window.onload = function() {

    // --- API Configuration ---
    const BASE_URL = 'http://localhost:8089';
    const API_ENDPOINT = '/api/fibonacci';

    // --- DOM Element Selection ---
    const stringInput = document.getElementById('stringInput');
    const submitButton = document.getElementById('submitButton');
    const errorBox = document.getElementById('errorBox');
    const errorMessage = document.getElementById('errorMessage');
    const editButtons = document.getElementById('editButtons');
    const cancelButton = document.getElementById('cancelButton');
    const clearButton = document.getElementById('clearButton');
    const stringList = document.getElementById('stringList');
    const loadingMessage = document.getElementById('loadingMessage');

    // --- Application State ---
    let isEditMode = false;
    let currentEditId = null;

    // --- Helper Functions ---
    // Displays an error message in the UI.
    function showError(message) {
        errorMessage.textContent = message;
        errorBox.classList.remove('hidden');
    }

    // Hides the error message.
    function hideError() {
        errorBox.classList.add('hidden');
        errorMessage.textContent = '';
    }
    
    // Renders the list of Fibonacci numbers on the page.
    function renderList(fibonacciNumbers) {
        stringList.innerHTML = ''; // Clear the current list.
        
        if (fibonacciNumbers.length === 0) {
            stringList.innerHTML = `<p class="text-center text-gray-500">No Fibonacci numbers saved yet.</p>`;
        } else {
            fibonacciNumbers.forEach(item => {
                const listItem = document.createElement('div');
                listItem.className = 'p-4 bg-white rounded-xl shadow-sm border border-gray-100 flex justify-between items-center transition-transform transform hover:scale-[1.01]';
                listItem.innerHTML = `
                    <div>
                        <p class="text-lg font-bold text-gray-800">
                            n = ${item.n}
                        </p>
                        <p class="text-md text-gray-600">
                            Fibonacci number = ${item.fibNumber}
                        </p>
                    </div>
                    <div class="flex space-x-2">
                        <button class="edit-btn px-4 py-2 bg-yellow-500 text-white font-semibold rounded-xl hover:bg-yellow-600 transition-colors" data-id="${item.id}" data-n="${item.n}">
                            Edit
                        </button>
                        <button class="delete-btn px-4 py-2 bg-red-500 text-white font-semibold rounded-xl hover:bg-red-600 transition-colors" data-id="${item.id}">
                            Delete
                        </button>
                    </div>
                `;
                stringList.appendChild(listItem);
            });
            // Attach event listeners to the new buttons.
            addListItemEventListeners();
        }
    }

    // Attaches click listeners to dynamically created buttons.
    function addListItemEventListeners() {
        document.querySelectorAll('.edit-btn').forEach(button => {
            button.addEventListener('click', (e) => {
                const id = e.target.getAttribute('data-id');
                const n = e.target.getAttribute('data-n');
                startEditMode(id, n);
            });
        });
        document.querySelectorAll('.delete-btn').forEach(button => {
            button.addEventListener('click', (e) => {
                const id = e.target.getAttribute('data-id');
                deleteItem(id);
            });
        });
    }

    // Clears the input field and resets the UI state.
    function clearInput() {
        stringInput.value = '';
        hideError();
        endEditMode();
    }
    
    // Starts the edit mode UI.
    function startEditMode(id, n) {
        isEditMode = true;
        currentEditId = id;
        stringInput.value = n;
        submitButton.textContent = 'Update';
        editButtons.classList.remove('hidden');
    }

    // Ends the edit mode UI.
    function endEditMode() {
        isEditMode = false;
        currentEditId = null;
        submitButton.textContent = 'Submit';
        editButtons.classList.add('hidden');
    }
    
    // --- API Interactions ---
    // Fetches all Fibonacci numbers from the backend.
    async function fetchFibonacciNumbers() {
        loadingMessage.classList.remove('hidden');
        try {
            const response = await fetch(`${BASE_URL}${API_ENDPOINT}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const numbers = await response.json();
            renderList(numbers);
        } catch (error) {
            console.error("Error fetching Fibonacci numbers:", error);
            showError("Failed to load numbers. Please ensure your backend is running.");
        } finally {
            loadingMessage.classList.add('hidden');
        }
    }

    // Adds a new item via a POST request.
    async function addItem(n) {
        try {
            const response = await fetch(`${BASE_URL}${API_ENDPOINT}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ n: n })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            stringInput.value = '';
            hideError();
            fetchFibonacciNumbers(); // Refresh the list
        } catch (error) {
            console.error("Error adding item:", error);
            showError("Failed to add the number. Please try again.");
        }
    }
    
    // Updates an item via a PUT request.
    async function updateItem(id, n) {
        try {
            const response = await fetch(`${BASE_URL}${API_ENDPOINT}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ id: id, n: n }) // Send both ID and N in the body
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            clearInput();
            hideError();
            fetchFibonacciNumbers(); // Refresh the list
        } catch (error) {
            console.error("Error updating item:", error);
            showError("Failed to update the number. Please try again.");
        }
    }
    
    // Deletes an item via a DELETE request.
    async function deleteItem(id) {
        try {
            const response = await fetch(`${BASE_URL}${API_ENDPOINT}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ id: id }) // Send ID in the body
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            hideError();
            fetchFibonacciNumbers(); // Refresh the list
        } catch (error) {
            console.error("Error deleting item:", error);
            showError("Failed to delete the number. Please try again.");
        }
    }

    // --- Event Listeners ---
    // Handles the form submission.
    submitButton.addEventListener('click', () => {
        const inputValue = stringInput.value.trim();
        if (inputValue === '') {
            showError("Please enter a number.");
            return;
        }

        const n = parseInt(inputValue, 10);
        if (isNaN(n) || n < 0) {
            showError("Please enter a valid non-negative integer.");
            return;
        }

        if (isEditMode) {
            updateItem(currentEditId, n);
        } else {
            addItem(n);
        }
    });

    // Cancels the edit mode.
    cancelButton.addEventListener('click', () => {
        clearInput();
    });

    // Clears the input field.
    clearButton.addEventListener('click', () => {
        stringInput.value = '';
        hideError();
    });
    
    // Fetch data on page load
    fetchFibonacciNumbers();
};
