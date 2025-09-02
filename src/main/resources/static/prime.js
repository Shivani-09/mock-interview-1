document.addEventListener('DOMContentLoaded', () => {

    // Get DOM elements
    const stringInput = document.getElementById('stringInput');
    const submitButton = document.getElementById('submitButton');
    const loadingMessage = document.getElementById('loadingMessage');
    const errorBox = document.getElementById('errorBox');
    const errorMessage = document.getElementById('errorMessage');
    const primeForm = document.getElementById('primeForm');
    const csvFile = document.getElementById('csvFile');
    const primeResultsBody = document.getElementById('prime-results-body');
    const editButtons = document.getElementById('editButtons');
    const cancelButton = document.getElementById('cancelButton');
    const clearButton = document.getElementById('clearButton');

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
        if (editButtons) {
            editButtons.classList.add('hidden');
        }
        editingId = null;
        hideError();
        if (csvFile) {
            csvFile.value = '';
        }
    };

    function showToast(message) {
        const toast = document.getElementById('toast-message');
        if (toast) {
            toast.innerText = message;
            toast.style.display = 'block';
            toast.style.opacity = '1';

            setTimeout(() => {
                toast.style.opacity = '0';
                setTimeout(() => {
                    toast.style.display = 'none';
                }, 500);
            }, 5000);
        }
    }

    // --- Fetch and Render Results ---

    const fetchPrimeResults = async () => {
        if (loadingMessage) {
            loadingMessage.classList.remove('hidden');
        }
        primeResultsBody.innerHTML = ''; // Clear previous results
        
        try {
            const response = await fetch('http://localhost:8089/api/prime');
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const primes = await response.json();
            
            if (primes.length > 0) {
                primes.forEach(prime => {
                    const row = document.createElement('tr');
                    
                    const idCell = document.createElement('td');
                    idCell.innerText = prime.id;
                    row.appendChild(idCell);

                    const inputCell = document.createElement('td');
                    inputCell.innerText = prime.input;
                    row.appendChild(inputCell);

                    const primeCheckCell = document.createElement('td');
                    primeCheckCell.innerHTML = `<span class="${prime.primeCheck ? 'is-prime' : 'not-prime'}">${prime.primeCheck ? 'Prime' : 'Not Prime'}</span>`;
                    row.appendChild(primeCheckCell);

                    const s3PathCell = document.createElement('td');
                    if (prime.s3Path) {
                        s3PathCell.innerHTML = `<a href="${prime.s3Path}" target="_blank">View File</a>`;
                    } else {
                        s3PathCell.innerText = '-';
                    }
                    row.appendChild(s3PathCell);
                    
                    const actionsCell = document.createElement('td');
                    actionsCell.innerHTML = `<a href="#" class="edit-btn" data-id="${prime.id}" data-input="${prime.input}">Edit</a> | <a href="#" class="delete-btn" data-id="${prime.id}">Delete</a>`;
                    row.appendChild(actionsCell);

                    primeResultsBody.appendChild(row);
                });
            } else {
                const row = document.createElement('tr');
                const cell = document.createElement('td');
                cell.setAttribute('colspan', '5');
                cell.innerText = 'No prime numbers found.';
                row.appendChild(cell);
                primeResultsBody.appendChild(row);
            }
        } catch (error) {
            console.error('Failed to fetch prime results:', error);
            const row = document.createElement('tr');
            const cell = document.createElement('td');
            cell.setAttribute('colspan', '5');
            cell.innerText = 'Failed to load results. Please try again later.';
            row.appendChild(cell);
            primeResultsBody.appendChild(row);
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

            await fetchPrimeResults();
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

            await fetchPrimeResults();
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

            await fetchPrimeResults();
        } catch (error) {
            showError(error.message);
            console.error('Delete error:', error);
        }
    };

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
                body: formData
            });

            if (response.ok) {
                const message = await response.text();
                showToast(message);
                await fetchPrimeResults(); 
                resetForm(); 
            } else {
                const error = await response.text();
                showToast("Error: " + error);
            }
        } catch (error) {
            showToast("Network Error: " + error.message);
        } finally {
            if (loadingMessage) {
                loadingMessage.classList.add('hidden');
            }
        }
    };

    // --- Event Listeners ---

    if (primeForm) {
        primeForm.addEventListener('submit', (e) => {
            e.preventDefault();

            if (csvFile && csvFile.files.length > 0) {
                const file = csvFile.files[0];
                const allowedExtensions = ['.csv', '.xlsx', '.txt'];
                const fileExtension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'));

                if (!allowedExtensions.includes(fileExtension)) {
                    showError('The selected file is not a CSV, TXT, or XLSX file.');
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

    // Use event delegation for dynamically created buttons
    if (primeResultsBody) {
        primeResultsBody.addEventListener('click', (e) => {
            const target = e.target;
            if (target.classList.contains('edit-btn')) {
                e.preventDefault();
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
                e.preventDefault();
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
    fetchPrimeResults();
});