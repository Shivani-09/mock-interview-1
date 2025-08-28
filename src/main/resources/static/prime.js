document.addEventListener('DOMContentLoaded', () => {

    // Get DOM elements
    const stringInput = document.getElementById('stringInput');
    const submitButton = document.getElementById('submitButton');
    const stringList = document.getElementById('stringList');
    const loadingMessage = document.getElementById('loadingMessage');
    const errorBox = document.getElementById('errorBox');
    const errorMessage = document.getElementById('errorMessage');
    const editButtons = document.getElementById('editButtons');
    const cancelButton = document.getElementById('cancelButton');
    const clearButton = document.getElementById('clearButton');
    const primeForm = document.getElementById('primeForm');
    const csvFile = document.getElementById('csvFile');

    // API endpoint constants
    const BASE_URL = 'http://localhost:8089/api/prime';
    const UPDATE_URL = 'http://localhost:8089/api/prime/database';
    const DELETE_URL = 'http://localhost:8089/api/prime';
    const UPLOAD_URL = 'http://localhost:8089/api/uploadFile';

    // Global state for edit mode
    let editingId = null;

    // --- Helper Functions ---

    const showError = (message) => {
        if (errorMessage && errorBox) {
            errorMessage.textContent = message;
            errorBox.classList.remove('hidden');
        }
    };

    const hideError = () => {
        if (errorMessage && errorBox) {
            errorBox.classList.add('hidden');
            errorMessage.textContent = '';
        }
    };

    const resetForm = () => {
        stringInput.value = '';
        submitButton.textContent = 'Submit';
        if (editButtons) { // Check if the element exists
            editButtons.classList.add('hidden');
        }
        editingId = null;
        hideError();
        if (csvFile) {
            csvFile.value = '';
        }
    };

    const renderPrimeNumbers = (primes) => {
        stringList.innerHTML = ''; // Clear previous list
        if (primes.length === 0) {
            stringList.innerHTML = '<p class="text-gray-500 italic">No results yet. Enter a number to start!</p>';
            return;
        }

        primes.forEach(prime => {
            const item = document.createElement('div');
            item.className = 'flex items-center justify-between p-4 bg-white rounded-xl shadow-sm border border-gray-200';
            item.innerHTML = `
                <div>
                    <span class="font-semibold text-gray-500 text-sm">ID: ${prime.id}</span>
                    <span class="font-semibold text-gray-800">Number: ${prime.input}</span>
                    <span class="ml-4 font-semibold text-sm ${prime.primeCheck ? 'text-green-600' : 'text-red-600'}">
                        ${prime.primeCheck ? 'Prime' : 'Not Prime'}
                    </span>
                </div>
                <div class="flex space-x-2">
                    <button class="edit-btn px-4 py-2 text-sm font-medium text-blue-600 rounded-lg hover:bg-blue-100 transition-colors" data-id="${prime.id}" data-input="${prime.input}">Edit</button>
                    <button class="delete-btn px-4 py-2 text-sm font-medium text-red-600 rounded-lg hover:bg-red-100 transition-colors" data-id="${prime.id}">Delete</button>
                </div>
            `;
            stringList.appendChild(item);
        });
    };

    const fetchPrimeNumbers = async () => {
        if (loadingMessage) {
            loadingMessage.classList.remove('hidden');
        }
        try {
            const response = await fetch(BASE_URL);
            if (!response.ok) {
                throw new Error('Failed to fetch prime numbers.');
            }
            const primes = await response.json();
            renderPrimeNumbers(primes);
        } catch (error) {
            showError('Could not connect to the API. Please check the server.');
            console.error('Fetch error:', error);
        } finally {
            if (loadingMessage) {
                loadingMessage.classList.add('hidden');
            }
        }
    };

    // --- CRUD Operations ---

    const savePrimeNumber = async (number) => {
        try {
            const response = await fetch(BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ input: parseInt(number, 10) })
            });

            if (!response.ok) {
                throw new Error('Failed to save the number.');
            }

            await fetchPrimeNumbers();
            resetForm();
        } catch (error) {
            showError(error.message);
            console.error('Save error:', error);
        }
    };

    const updatePrimeNumber = async (id, number) => {
        try {
            const response = await fetch(UPDATE_URL, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id, input: parseInt(number, 10) })
            });

            if (!response.ok) {
                throw new Error('Failed to update the number.');
            }

            await fetchPrimeNumbers();
            resetForm();
        } catch (error) {
            showError(error.message);
            console.error('Update error:', error);
        }
    };

    const deletePrimeNumber = async (id) => {
        try {
            const response = await fetch(DELETE_URL, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: parseInt(id, 10) })
            });

            if (!response.ok) {
                throw new Error('Failed to delete the number.');
            }

            await fetchPrimeNumbers();
        } catch (error) {
            showError(error.message);
            console.error('Delete error:', error);
        }
    };

    // --- New Upload Function ---

    const uploadFile = async (file) => {
        if (loadingMessage) {
            loadingMessage.classList.remove('hidden');
        }
        hideError();

        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch(UPLOAD_URL, {
                method: 'POST',
                body: formData,
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Failed to upload the file.');
            }

            const successMessage = await response.text();
            alert(successMessage);

            await fetchPrimeNumbers();
            resetForm();
        } catch (error) {
            showError(error.message);
            console.error('Upload error:', error);
        } finally {
            if (loadingMessage) {
                loadingMessage.classList.add('hidden');
            }
        }
    };

    // --- Event Listeners ---

    // Use a single event listener on the form to handle both scenarios
    if (primeForm) {
        primeForm.addEventListener('submit', (e) => {
            e.preventDefault();

            if (csvFile && csvFile.files.length > 0) {
                const file = csvFile.files[0];

                if (file.type && file.type !== 'text/csv' && !file.name.endsWith('.csv')) {
                     showError('The selected file is not a CSV file.');
                     return;
                }
                uploadFile(file);
            } else {
                const number = stringInput.value.trim();
                if (!number || isNaN(number)) {
                    showError('Please enter a valid number.');
                    return;
                }
                if (editingId) {
                    updatePrimeNumber(editingId, number);
                } else {
                    savePrimeNumber(number);
                }
            }
        });
    }

    if (stringList) {
        stringList.addEventListener('click', (e) => {
            const target = e.target;
            if (target.classList.contains('edit-btn')) {
                editingId = target.dataset.id;
                const input = target.dataset.input;
                stringInput.value = input;
                submitButton.textContent = 'Update';
                if (editButtons) {
                    editButtons.classList.remove('hidden');
                }
                stringInput.focus();
                hideError();
            } else if (target.classList.contains('delete-btn')) {
                if (confirm('Are you sure you want to delete this record?')) {
                    deletePrimeNumber(target.dataset.id);
                }
            }
        });
    }

    if (cancelButton) {
        cancelButton.addEventListener('click', () => {
            resetForm();
        });
    }

    if (clearButton) {
        clearButton.addEventListener('click', () => {
            stringInput.value = '';
            stringInput.focus();
        });
    }
    
    // Initial fetch of prime numbers when the page loads
    fetchPrimeNumbers();
});