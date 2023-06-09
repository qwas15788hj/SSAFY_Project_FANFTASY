package com.a306.fanftasy.domain.nft.controller;

import com.a306.fanftasy.domain.nft.dto.NFTCreateDTO;
import com.a306.fanftasy.domain.nft.dto.NFTSourceTradeDTO;
import com.a306.fanftasy.domain.nft.dto.NFTTradeDTO;
import com.a306.fanftasy.domain.nft.dto.SaleDTO;
import com.a306.fanftasy.domain.nft.service.NFTService;
import com.a306.fanftasy.domain.nft.service.NFTSourceService;
import com.a306.fanftasy.domain.nft.service.PinataService;
import com.a306.fanftasy.domain.response.ResponseDefault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/nft")
@Slf4j
public class NFTController {
    private final NFTService nftService;
    private final NFTSourceService nftSourceService;
    private final PinataService pinataService;

    //1. NFT 생성
    @PostMapping
    public ResponseEntity<?> NFTAdd(@RequestParam("file") MultipartFile file, @RequestParam("info") String info, @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate){
        log.info("NFT 생성 요청 : " + info);
        ResponseDefault responseDefault = null;
        try{
            if(file==null || info==null ||endDate==null||endDate.isBefore(LocalDateTime.now())){
                responseDefault = ResponseDefault.builder().success(false).messege("File or Info or EndDate is null").build();
                return ResponseEntity.badRequest().body(responseDefault);
            }
            //받아온 file과 info를 통해서 IPFS에 저장하고 CID를 반환함.
            //해당 정보를 DB에 저장
            log.info("파일 pinata 저장 시작");
            String fileCID = pinataService.fileToPinata(file);
            log.info("fileCID : " + fileCID);
            NFTCreateDTO nftCreateDTO = pinataService.jsonToPinata(info, fileCID); //메타데이터를 저장하고, source를 저장하기
            log.info("NFTCreateDTO : " + nftCreateDTO.toString());
            //CID를 통해서 스마트 컨트랙트 호출하고 발행량 만큼 NFT발급
            nftService.addNFT(nftCreateDTO, endDate);
            responseDefault = ResponseDefault.builder().success(true).messege("SUCCESS").build();
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            responseDefault = ResponseDefault.builder().success(false).messege(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    //2. 드롭스에서 리스트 반환
    @GetMapping("/drops")
    public ResponseEntity<?> NFTDropsList(@RequestParam int orderType, @RequestParam int page,@RequestParam String keyword){
        log.info("NFT 드롭스 페이지 리스트 요청");
        log.info("orderType : " + orderType + ", page : " + page + ", keyword : " + keyword);
        ResponseDefault responseDefault = null;
        try{
            responseDefault = ResponseDefault.builder()
                .messege("SUCCESS")
                .data(nftSourceService.getNFTSourceList(orderType, page, keyword))
                .success(true).build();
            log.info("NFT Source List 조회 성공");
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            log.error("NFT Source List 조회 실패");
            responseDefault = ResponseDefault.builder()
                .success(false)
                .messege("FAIL").build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    // 마켓 플레이스에서 리스트 반환
    @GetMapping("/market")
    public ResponseEntity<?> NFTMarketList(@RequestParam int orderType, @RequestParam int saleType, @RequestParam String keyword) {
        ResponseDefault responseDefault = null;
        try {
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("SUCCESS")
                    .data(nftService.getNFTList(orderType, saleType, keyword))
                    .build();
            return ResponseEntity.ok().body(responseDefault);
        } catch (Exception e) {
            log.error("NFT Market 조회 실패");
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("FAIL")
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    // 마켓 플레이스 NFT 상세 정보
    @GetMapping("/market/{nftSourceId}")
    public ResponseEntity<?> NFTMarketDetail(@PathVariable("nftSourceId") Long nftSourceId, @RequestParam(value = "userId", required = false) Long userId) {
        log.info("NFT 마켓 페이지 상세 요청 : " + nftSourceId);
        log.info("userId 번호 조회 : " + userId);
        ResponseDefault responseDefault = null;
        try {
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("SUCCESS")
                    .data(nftService.getNFTDetail(nftSourceId, userId))
                    .build();
            return ResponseEntity.ok().body(responseDefault);
        } catch (Exception e) {
            log.error("NFT Market Detail 조회 실패");
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("FAIL")
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    //3. 아티스트가 올린 NFT 컨텐츠 상세 페이지 반환
    @GetMapping("/drops/{nftSourceId}")
    public ResponseEntity<?> NFTMarketDetail(@PathVariable("nftSourceId") long nftSourceId, @RequestParam(value = "userId", required = false) Long userId){
        log.info("NFT 마켓 페이지 상세 요청 : " + nftSourceId);
        log.info("userId 번호 조회 : " + userId);
        ResponseDefault responseDefault = null;
        try{
            responseDefault = ResponseDefault.builder()
                .messege("SUCCESS")
                .data(nftSourceService.getNFTSourceDetail(nftSourceId, userId))
                .success(true).build();
            log.info("NFT 조회 성공");
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            log.error("NFT 조회 실패");
            responseDefault = ResponseDefault.builder()
                .success(false)
                .messege("FAIL").build();

            return ResponseEntity.badRequest().body(responseDefault);
        }
    }
//    //4. 아티스트가 올린 NFT 최초 거래
//    @PutMapping("/drops")
//    public ResponseEntity<?> NFTFirstTrade(@RequestBody NFTSourceTradeDTO nftSourceTradeDTO){
//        log.info("NFT 거래 발생 : " + nftSourceTradeDTO.toString());
//        ResponseDefault responseDefault = null;
//        try{
//            nftSourceService.modifyNFTSource(nftSourceTradeDTO);
//            responseDefault = ResponseDefault.builder()
//                .success(true)
//                .messege("SUCCESS")
//                .build();
//            log.info("거래 반영 성공");
//            return ResponseEntity.ok().body(responseDefault);
//        }catch (Exception e){
//            log.error("거래 실패");
//            responseDefault = ResponseDefault.builder()
//                .success(false)
//                .messege("FAIL")
//                .build();
//            return ResponseEntity.badRequest().body(responseDefault);
//        }
//    }

    //5. 아티스트 페이지에서 발매된 NFT목록 반환
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<?> NFTArtistList(@PathVariable long artistId){
        log.info("NFT 아티스트 페이지 목록 요청 : " + artistId);
        ResponseDefault responseDefault = null;
        try{
            responseDefault = ResponseDefault.builder()
                .messege("SUCCESS")
                .data(nftSourceService.getNFTSourceListByArtist(artistId))
                .success(true).build();
            log.info("NFT 조회 성공");
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            log.error("NFT 조회 실패");
            responseDefault = ResponseDefault.builder()
                .success(false)
                .messege("FAIL").build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    //6. 회원 소유 NFT목록 반환
    @GetMapping("/user/{ownerId}")
    public ResponseEntity<?> NFTUserList(@PathVariable long ownerId, @RequestParam("type") int type){
        log.info("NFT 개인 페이지 목록 요청 : " + ownerId);
        ResponseDefault responseDefault = null;
        try{
            responseDefault = ResponseDefault.builder()
                .messege("SUCCESS")
                .data(nftService.getNFTListByOwnerId(ownerId, type))
                .success(true).build();
            log.info("NFT 조회 성공");
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            log.error("NFT 조회 실패");
            responseDefault = ResponseDefault.builder()
                .success(false)
                .messege("FAIL").build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    //7. 개인이 보유한 NFT 상세
    @GetMapping("/{nftId}")
    public ResponseEntity<?> NFTDetail(@PathVariable long nftId){
        log.info("NFT 조회 요청 : " + nftId);
        ResponseDefault responseDefault = null;
        try{
            responseDefault = ResponseDefault.builder()
                .messege("SUCCESS")
                .data(nftService.getNFT(nftId))
                .success(true).build();
            log.info("NFT 조회 성공");
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            log.error("NFT 조회 실패");
            responseDefault = ResponseDefault.builder()
                .success(false)
                .messege("FAIL").build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    //8. NFT 개인 거래 등록
    @PutMapping("/resell")
    public ResponseEntity<?> NFTResell(@RequestBody SaleDTO saleDTO){
        log.info("NFT 리셀 등록 : " + saleDTO.toString());
        ResponseDefault responseDefault = null;
        try{
            nftService.resell(saleDTO);
            responseDefault = ResponseDefault.builder()
                .success(true)
                .messege("SUCCESS")
                .build();
            log.info("거래 반영 성공");
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            log.error("거래 실패");
            responseDefault = ResponseDefault.builder()
                .success(false)
                .messege("FAIL")
                .build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    //9. NFT 구매
    @PutMapping("/buy")
    public ResponseEntity<?> NFTTrade(@RequestBody NFTTradeDTO nftTradeDTO){
        log.info("NFT 거래 발생 : " + nftTradeDTO.toString());
        ResponseDefault responseDefault = null;
        try{
            nftService.modifyNFT(nftTradeDTO);
            responseDefault = ResponseDefault.builder()
                .success(true)
                .messege("SUCCESS")
                .build();
            log.info("거래 반영 성공");
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            log.error("거래 실패");
            responseDefault = ResponseDefault.builder()
                .success(false)
                .messege("FAIL")
                .build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }


    // 리셀하기 위한 리셀 페이지 정보 요청
    @GetMapping("/resell/{nftId}")
    public ResponseEntity<?> NFTReSell(@PathVariable("nftId") Long nftId) {
        ResponseDefault responseDefault = null;
        try {
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("SUCCESS")
                    .data(nftService.nftReSell(nftId))
                    .build();
            return ResponseEntity.ok().body(responseDefault);
        } catch (Exception e) {
            log.error("리셀 정보 불러오기 실패");
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("FAIL")
                    .build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    //10. nft drop detail 페이지에서 "구매하기" 눌렀을때 판매될 nftId와 sale contract address 반환
    @GetMapping("/buy/{nftSourceId}")
    public ResponseEntity<?> NFTfirstSale(@PathVariable long nftSourceId){
        log.info("NFT Source 구매 요청 : " + nftSourceId);
        ResponseDefault responseDefault = null;
        try{
            responseDefault = ResponseDefault.builder()
                .messege("SUCCESS")
                .data(nftService.getNFTfirstSale(nftSourceId))
                .success(true).build();
            log.info("NFT 조회 성공");
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            log.error("NFT 조회 실패");
            responseDefault = ResponseDefault.builder()
                .success(false)
                .messege(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }

    //11. NFT resell로 등록된 상품 판매 취소
    @GetMapping("/cancel/{nftId}")
    public ResponseEntity<?> resellCancel(@PathVariable long nftId){
        log.info("NFT 판매 취소 요청 : " + nftId);
        ResponseDefault responseDefault = null;
        try{
            nftService.resellCancel(nftId);
            responseDefault = ResponseDefault.builder()
                .success(true)
                .messege("SUCCESS")
                .build();
            log.info("거래 취소 성공");
            return ResponseEntity.ok().body(responseDefault);
        }catch (Exception e){
            log.error("거래 취소 실패");
            responseDefault = ResponseDefault.builder()
                .success(false)
                .messege(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDefault);
        }
    }
}
