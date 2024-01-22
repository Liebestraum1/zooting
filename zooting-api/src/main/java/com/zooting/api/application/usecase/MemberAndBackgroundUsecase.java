package com.zooting.api.application.usecase;

import com.zooting.api.application.dto.request.MemberAndBackgroundReq;
import com.zooting.api.application.dto.response.MemberAndBackgroundRes;
import com.zooting.api.domain.background.dao.BackgroundInventoryRepository;
import com.zooting.api.domain.background.dao.BackgroundRepository;
import com.zooting.api.domain.background.entity.Background;
import com.zooting.api.domain.background.entity.BackgroundInventory;
import com.zooting.api.domain.member.dao.MemberRepository;
import com.zooting.api.domain.member.entity.Member;
import com.zooting.api.global.common.code.ErrorCode;
import com.zooting.api.global.exception.BaseExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberAndBackgroundUsecase {
    final private MemberRepository memberRepository;
    final private BackgroundRepository backgroundRepository;
    final private BackgroundInventoryRepository backgroundInventoryRepository;

    public Boolean buyBackgroundImg(String userId, MemberAndBackgroundReq backgroundReq) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(()->new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        Long memberPoints = member.getPoint();
        Background background = backgroundRepository.findById(backgroundReq.backgroundId())
                .orElseThrow(()-> new BaseExceptionHandler(ErrorCode.NOT_FOUND_ERROR));

        // 이미 샀던거면 return false
        List<BackgroundInventory> backgroundInventories = member.getMyBackgrounds();
        for(var bgImg : backgroundInventories) {
            if (bgImg.getBackground().getId() == backgroundReq.backgroundId()) {
                return false;
            }
        }
        // 가격보다 포인트가 적으면 return false
        if (memberPoints < background.getPrice()) {
            return false;
        }else {
            // 포인트 차감
            member.setPoint(memberPoints - background.getPrice());
            memberRepository.save(member);
            // 인벤토리 추가
            BackgroundInventory bgInventory = new BackgroundInventory();
            bgInventory.setBackground(background);
            bgInventory.setMember(member);
            backgroundInventoryRepository.save(bgInventory);
            return true;
        }

    }
    public List<MemberAndBackgroundRes> findAllBackgroundInventory(String userId) {
        Member member = memberRepository.findMemberByEmail(userId).orElseThrow(()->
                new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        List<MemberAndBackgroundRes> backgroundResList = backgroundInventoryRepository.findAllByMember(member)
                .stream().map(back-> new MemberAndBackgroundRes(
                        back.getBackground().getFile().getFileName(),
                        back.getBackground().getFile().getImg_url(),
                        back.getBackground().getPrice()) ).toList();
        return backgroundResList;
    }
}
