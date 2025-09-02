package com.example.mockInterview.service;

import com.example.mockInterview.controller.SwapDto;

public class UpdateSwapRequest {
	
    private Long id;
    private SwapDto swapDto;

    // Default constructor for deserialization
    public UpdateSwapRequest() {
    }

    public UpdateSwapRequest(Long id, SwapDto swapDto) {
        this.id = id;
        this.swapDto = swapDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SwapDto getSwapDto() {
        return swapDto;
    }

    public void setSwapDto(SwapDto swapDto) {
        this.swapDto = swapDto;
    }
}
