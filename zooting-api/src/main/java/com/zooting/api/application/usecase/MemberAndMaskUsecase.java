package com.zooting.api.application.usecase;

import com.zooting.api.application.dto.request.MemberAndMaskReq;
import com.zooting.api.application.dto.response.MemberAndBackgroundRes;
import com.zooting.api.application.dto.response.MemberAndMaskRes;
import com.zooting.api.domain.background.entity.Background;
import com.zooting.api.domain.background.entity.BackgroundInventory;
import com.zooting.api.domain.mask.dao.MaskInventoryRepository;
import com.zooting.api.domain.mask.dao.MaskRepository;
import com.zooting.api.domain.mask.entity.Mask;
import com.zooting.api.domain.mask.entity.MaskInventory;
import com.zooting.api.domain.member.dao.MemberRepository;
import com.zooting.api.domain.member.entity.AdditionalInfo;
import com.zooting.api.domain.member.entity.Member;
import com.zooting.api.global.common.code.ErrorCode;
import com.zooting.api.global.exception.BaseExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberAndMaskUsecase {
    final private MemberRepository memberRepository;
    final private MaskRepository maskRepository;
    final private MaskInventoryRepository maskInventoryRepository;

    public Boolean buyMask(String userId, MemberAndMaskReq maskReq) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(()->new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        Long memberPoints = member.getPoint();
        Mask mask = maskRepository.findById(maskReq.maskId())
                .orElseThrow(()-> new BaseExceptionHandler(ErrorCode.NOT_FOUND_ERROR));
        // 이미 샀던거면 return false
        List<MaskInventory> maskInventories = member.getMyMasks();
        for(var myMask : maskInventories) {
            if (myMask.getMask().getId() == maskReq.maskId()) {
                return false;
            }
        }

        // 가격보다 포인트가 적거나 자신의 동물상이 아니라 return false
        if (memberPoints < mask.getPrice() || ! member.getAdditionalInfo().getAnimal().equals(mask.getAnimal())) {
            return false;
        }else {
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

    public List<MemberAndMaskRes> findAllMaskInventory(String userId) {
        Member member = memberRepository.findMemberByEmail(userId).orElseThrow(()->
                new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        List<MemberAndMaskRes> maskResList = maskInventoryRepository.findAllByMember(member)
                .stream().map(myMask-> new MemberAndMaskRes(
                        myMask.getMask().getAnimal(),
                        myMask.getMask().getDescription(),
                        myMask.getMask().getPrice(),
                        myMask.getMask().getFile().getFileName(),
                        myMask.getMask().getFile().getImg_url()
                         )).toList();
        return maskResList;
    }
}
