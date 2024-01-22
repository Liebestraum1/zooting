package com.zooting.api.domain.mask.api;


import com.zooting.api.domain.mask.application.MaskService;
import com.zooting.api.domain.mask.dto.response.MaskRes;
import com.zooting.api.global.common.BaseResponse;
import com.zooting.api.global.common.code.SuccessCode;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/items/mask")
@RequiredArgsConstructor
public class MaskController {
    final private MaskService maskService;

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/")
    public ResponseEntity<BaseResponse<List<MaskRes>>> findAllMasks()  {
        List<MaskRes> result = maskService.findAllMask();
        return BaseResponse.success(
                SuccessCode.SELECT_SUCCESS,
                result
        );
    }

}
