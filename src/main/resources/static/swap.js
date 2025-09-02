const API_BASE_URL = 'http://localhost:8089/swap';
    
document.addEventListener('DOMContentLoaded', () => {
    const swapForm = document.getElementById('swapForm');
    const number1Input = document.getElementById('number1');
    const number2Input = document.getElementById('number2');
    const fileTypeInput = document.getElementById('fileType');
    const submitButton = document.getElementById('submitButton');
    const cancelButton = document.getElementById('cancelButton');
    const swapIdInput = document.getElementById('swapId');
    const swapResultsBody = document.getElementById('swap-results-body');
    const toastMessage = document.getElementById('toast-message');

    fetchSwaps();

    swapForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const file = fileTypeInput.files[0];
        const number1 = number1Input.value.trim();
        const number2 = number2Input.value.trim();
        const swapId = swapIdInput.value;

        if (file) {
            const formData = new FormData();
            formData.append('file', file);
            try {
                submitButton.disabled = true;
                submitButton.textContent = 'Uploading...';
                const response = await fetch(`${API_BASE_URL}/upload`, {
                    method: 'POST',
                    body: formData,
                });
                if (!response.ok) {
                    throw new Error('Failed to upload file.');
                }
                const message = await response.text();
                showToast(message, 'bg-green-500');
                fetchSwaps();
            } catch (error) {
                console.error('File upload failed:', error);
                showToast(`Error: ${error.message}`, 'bg-red-500');
            } finally {
                submitButton.disabled = false;
                submitButton.textContent = 'Submit';
            }
        } else if (number1 && number2) {
            const swapData = {
                original_number1: parseInt(number1, 10),
                original_number2: parseInt(number2, 10)
            };
            if (swapId) {
                // FIX: Changed the JSON payload to match the backend's UpdateSwapRequest object
                const updateData = {
                    id: parseInt(swapId, 10),
                    swapDto: swapData
                };

                try {
                    submitButton.disabled = true;
                    submitButton.textContent = 'Updating...';
                    const response = await fetch(`${API_BASE_URL}/sql`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(updateData),
                    });
                    if (!response.ok) {
                        throw new Error('Failed to update numbers.');
                    }
                    showToast('Numbers updated successfully!', 'bg-green-500');
                    fetchSwaps();
                    resetForm();
                } catch (error) {
                    console.error('Update failed:', error);
                    showToast(`Error: ${error.message}`, 'bg-red-500');
                } finally {
                    submitButton.disabled = false;
                    submitButton.textContent = 'Submit';
                }
            } else {
                try {
                    submitButton.disabled = true;
                    submitButton.textContent = 'Swapping...';
                    const response = await fetch(`${API_BASE_URL}/sql`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(swapData),
                    });
                    if (!response.ok) {
                        throw new Error('Failed to swap numbers.');
                    }
                    showToast('Numbers swapped and saved successfully!', 'bg-green-500');
                    fetchSwaps();
                } catch (error) {
                    console.error('Swap failed:', error);
                    showToast(`Error: ${error.message}`, 'bg-red-500');
                } finally {
                    submitButton.disabled = false;
                    submitButton.textContent = 'Submit';
                    number1Input.value = '';
                    number2Input.value = '';
                }
            }
        } else {
            showToast('Please enter both numbers or select a file.', 'bg-red-500');
        }
    });
    
    cancelButton.addEventListener('click', resetForm);

    async function fetchSwaps() {
        try {
            const response = await fetch(`${API_BASE_URL}/sql`);
            if (!response.ok) {
                throw new Error('Failed to fetch swap data.');
            }
            const data = await response.json();
            renderSwaps(data);
        } catch (error) {
            console.error('Error fetching swaps:', error);
            showToast('Failed to load data. Please check the server.', 'bg-red-500');
        }
    }

    function renderSwaps(swaps) {
        swapResultsBody.innerHTML = '';
        if (swaps.length === 0) {
            swapResultsBody.innerHTML = '<tr><td colspan="5" class="text-center py-4 text-gray-500">No swap records found.</td></tr>';
            return;
        }
        swaps.forEach(swap => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap">${swap.id}</td>
                <td class="px-6 py-4 whitespace-nowrap">(${swap.original_number1}, ${swap.original_number2})</td>
                <td class="px-6 py-4 whitespace-nowrap">(${swap.swapped_number1}, ${swap.swapped_number2})</td>
                <td class="px-6 py-4 whitespace-nowrap overflow-hidden text-ellipsis">${swap.s3_path}</td>
                <td class="px-6 py-4 whitespace-nowrap text-center flex justify-center space-x-2">
                    <button class="edit-btn text-blue-500 hover:text-blue-700 font-bold py-1 px-3 rounded-full transition-colors" data-id="${swap.id}" data-num1="${swap.original_number1}" data-num2="${swap.original_number2}">
                        Edit
                    </button>
                    <button class="delete-btn text-red-500 hover:text-red-700 font-bold py-1 px-3 rounded-full transition-colors" data-id="${swap.id}">
                        Delete
                    </button>
                </td>
            `;
            swapResultsBody.appendChild(row);
        });

        // Add event listeners to the new buttons after they are created
        document.querySelectorAll('.edit-btn').forEach(button => {
            button.addEventListener('click', (e) => {
                const id = e.target.dataset.id;
                const num1 = e.target.dataset.num1;
                const num2 = e.target.dataset.num2;
                editSwap(id, num1, num2);
            });
        });

        document.querySelectorAll('.delete-btn').forEach(button => {
            button.addEventListener('click', (e) => {
                const id = e.target.dataset.id;
                deleteSwap(id);
            });
        });
    }

    // Now moved inside DOMContentLoaded to ensure it's in scope
    function editSwap(id, num1, num2) {
        number1Input.value = num1;
        number2Input.value = num2;
        swapIdInput.value = id;
        submitButton.textContent = 'Update';
        cancelButton.classList.remove('hidden');
    }

    // Now moved inside DOMContentLoaded to ensure it's in scope
    async function deleteSwap(id) {
        console.log(`Attempting to delete record with ID: ${id}`);
        
        try {
            const response = await fetch(`${API_BASE_URL}/sql`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ id: id }),
            });

            if (!response.ok) {
                throw new Error('Failed to delete the record.');
            }
            showToast('Record deleted successfully.', 'bg-green-500');
            fetchSwaps();
        } catch (error) {
            console.error('Error deleting swap:', error);
            showToast(`Error: ${error.message}`, 'bg-red-500');
        }
    }

    function resetForm() {
        swapForm.reset();
        swapIdInput.value = '';
        submitButton.textContent = 'Submit';
        cancelButton.classList.add('hidden');
    }
    
    function showToast(message, bgColor) {
        toastMessage.textContent = message;
        toastMessage.className = `toast-message ${bgColor}`;
        toastMessage.style.display = 'block';
        toastMessage.style.opacity = '1';

        setTimeout(() => {
            toastMessage.style.opacity = '0';
            setTimeout(() => {
                toastMessage.style.display = 'none';
            }, 500);
        }, 3000);
    }
});
