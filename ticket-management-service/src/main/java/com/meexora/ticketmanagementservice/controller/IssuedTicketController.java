package com.meexora.ticketmanagementservice.controller;


import com.meexora.common.response.ApiResponse;
import com.meexora.ticketmanagementservice.dto.request.TicketValidationRequest;
import com.meexora.ticketmanagementservice.dto.response.VerifiedTicketResponse;
import com.meexora.ticketmanagementservice.dto.response.VerifiedTicketStats;
import com.meexora.ticketmanagementservice.service.TicketVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class IssuedTicketController {
    private final TicketVerificationService ticketVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifiedTicketResponse>> verifyTicket(@RequestBody @Valid TicketValidationRequest request, @RequestHeader("X-User-Id") String userId) {
        VerifiedTicketResponse response = ticketVerificationService.verifyTicket(request);
        return ResponseEntity.ok(ApiResponse.success("Ticket verified successfully", response));
    }

    @GetMapping("/verify/{eventId}")
    public ResponseEntity<ApiResponse<VerifiedTicketStats>> getValidatedTickets(@PathVariable("eventId") String eventId) {
        int count = ticketVerificationService.countValidatedTickets(UUID.fromString(eventId));
        VerifiedTicketStats stats = new VerifiedTicketStats(count);
        return ResponseEntity.ok(ApiResponse.success("Validated tickets fetched", stats));
    }
}
