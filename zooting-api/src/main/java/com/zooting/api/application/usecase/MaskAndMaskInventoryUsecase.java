package com.zooting.api.application.usecase;

import com.zooting.api.application.dto.request.MemberAndMaskReq;
import com.zooting.api.application.dto.response.MemberAndMaskRes;
import com.zooting.api.domain.mask.dao.MaskInventoryRepository;
import com.zooting.api.domain.mask.dao.MaskRepository;
import com.zooting.api.domain.mask.entity.Mask;
import com.zooting.api.domain.mask.entity.MaskInventory;
import com.zooting.api.domain.member.dao.MemberRepository;
import com.zooting.api.domain.member.entity.Member;
import com.zooting.api.global.common.code.ErrorCode;
import com.zooting.api.global.exception.BaseExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaskAndMaskInventoryUsecase {
    final private MemberRepository memberRepository;
    final private MaskRepository maskRepository;
    final private MaskInventoryRepository maskInventoryRepository;

    @Transactional
    public Boolean buyMask(String userId, MemberAndMaskReq maskReq) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(()->new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        Long memberPoints = member.getPoint();
        Mask mask = maskRepository.findById(maskReq.maskId())
                .orElseThrow(()-> new BaseExceptionHandler(ErrorCode.NOT_FOUND_ERROR));

        // 이미 샀거나 가격보다 포인트가 적거나 자신의 동물상이 아니라 return false
        Boolean isInMaskInventory = maskInventoryRepository.existsByMaskIdAndMember(maskReq.maskId(), member);
        if (isInMaskInventory || memberPoints < mask.getPrice() || ! member.getAdditionalInfo().getAnimal().equals(mask.getAnimal())) {
            return false;
        }
        // 포인트 차감
        member.setPoint(memberPoints - mask.getPrice());
        memberRepository.save(member);
        // 인벤토리 추가
        MaskInventory maskInventory = new MaskInventory();
        maskInventory.setMask(mask);
        maskInventory.setMember(member);
        maskInventoryRepository.save(maskInventory);
        return true;

    }

}
