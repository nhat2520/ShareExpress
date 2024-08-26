package com.nhat.fileservice.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareRequest {
    private Long userID;
    private Long receiverID;
    private String type;
    private Long resourceID;
}
