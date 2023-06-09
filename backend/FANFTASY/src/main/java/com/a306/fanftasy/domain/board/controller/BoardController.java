package com.a306.fanftasy.domain.board.controller;

import com.a306.fanftasy.domain.board.dto.*;
import com.a306.fanftasy.domain.board.entity.Board;
import com.a306.fanftasy.domain.board.service.BoardService;
import com.a306.fanftasy.domain.board.service.ImageService;
import com.a306.fanftasy.domain.board.service.MyService;
import com.a306.fanftasy.domain.board.service.BoardS3Service;
import com.a306.fanftasy.domain.nft.entity.NFT;
import com.a306.fanftasy.domain.response.ResponseDefault;
import com.a306.fanftasy.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final ImageService imageService;
    private final MyService myService;
    private final BoardS3Service s3Service;

    // 구매글 등록
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseBoardSave(@RequestBody RequestPurchaseBoard requestPurchaseBoard) {
        ResponseDefault responseDefault = null;
        /**
         * User -> UserDto 변경 수정 필요
         */
        User user = boardService.findUserById(requestPurchaseBoard.getBoardWriteUserId());

        if (user == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("회원이 아닙니다.")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else {
            boardService.savePurchaseBoard(requestPurchaseBoard, user);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("등록 성공!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        }

    }

    // 구매글 상세정보 조회
    @GetMapping("/purchase/{id}")
    public ResponseEntity<?> purchaseBoardDetails(@PathVariable("id") Long id) {
        ResponseDefault responseDefault = null;
        try {
            ResponsePurchaseBoard purchaseBoard = boardService.findPurchaseBoardById(id);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("구매글 상세정보 불러오기 성공")
                    .data(purchaseBoard)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        } catch (Exception e) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("구매글 상세정보 불러오기 실패")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        }
    }

    //구매글 수정
    @PutMapping("/purchase/{id}")
    public ResponseEntity<?> purchaseBoardModify(@PathVariable("id") Long id,
                                                 @RequestBody RequestModifyPurchaseBoard requestModifyPurchaseBoard) {
        ResponseDefault responseDefault = null;
        try {
            boardService.modifyPurchaseBoard(id, requestModifyPurchaseBoard);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("구매글 수정 성공!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        } catch (Exception e) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("구매글 상세정보 불러오기 실패")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        }
    }
    
    //판매글 등록
    @PostMapping("/sales")
    public ResponseEntity<?> salesBoardSave(@RequestBody RequestSalesBoard requestSalesBoard) {
        ResponseDefault responseDefault = null;
        /**
         * User -> UserDto 변경 수정 필요
         */
        User user = boardService.findUserById(requestSalesBoard.getBoardWriteUserId());
        NFT nft = boardService.findNFTById(requestSalesBoard.getNftId());

        if (user == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("회원이 아닙니다.")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else if (nft == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("등록된 NFT가 없습니다.")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else {
            boardService.saveSalesBoard(requestSalesBoard, user, nft);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("등록 성공!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        }

    }

    // 판매글 상세정보 조회
    @GetMapping("/sales/{id}")
    public ResponseEntity<?> salesBoardDetails(@PathVariable("id") Long id) {
        ResponseDefault responseDefault = null;
        try {
            ResponseSalesBoard salesBoard = boardService.findSalesBoardById(id);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("판매글 상세정보 불러오기 성공")
                    .data(salesBoard)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        } catch (Exception e) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("판매글 상세정보 불러오기 실패")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        }
    }

    //판매글 수정
    @PutMapping("/sales/{id}")
    public ResponseEntity<?> purchaseBoardModify(@PathVariable("id") Long id,
                                                 @RequestBody RequestModifySalesBoard requestModifySalesBoard) {
        ResponseDefault responseDefault = null;
        try {
            boardService.modifySalesBoard(id, requestModifySalesBoard);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("판매글 수정 성공!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        } catch (Exception e) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("판매글 상세정보 불러오기 실패")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        }
    }

    // 게시글 등록
    @PostMapping("/article")
    public ResponseEntity<?> articleBoardSave(@RequestBody RequestArticleBoard requestArticleBoard) {
        ResponseDefault responseDefault = null;
        /**
         * User -> UserDto 변경 수정 필요
         */
        User user = boardService.findUserById(requestArticleBoard.getBoardWriteUserId());

        if (user == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("회원이 아닙니다.")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else {
            boardService.saveArticleBoard(requestArticleBoard, user);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("게시글 등록 성공!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        }
    }

    // 게시글 상세정보 조회
    @GetMapping("/article/{id}")
    public ResponseEntity<?> articleBoardDetails(@PathVariable("id") Long id) {
        ResponseDefault responseDefault = null;
        try {
            ResponseArticleBoard articleBoard = boardService.findArticleBoardById(id);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("게시글 상세정보 불러오기 성공")
                    .data(articleBoard)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        } catch (Exception e) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("게시글 상세정보 불러오기 실패")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        }
    }

    //게시글 수정
    @PutMapping("/article/{id}")
    public ResponseEntity<?> purchaseBoardModify(@PathVariable("id") Long id,
                                                 @RequestBody RequestModifyArticleBoard requestModifyArticleBoard) {
        ResponseDefault responseDefault = null;
        try {
            boardService.modifyArticleBoard(id, requestModifyArticleBoard);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("게시글 수정 성공!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        } catch (Exception e) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("게시글 상세정보 불러오기 실패")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        }
    }

    //구매글, 판매글, 게시물 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> boardRemove(@PathVariable("id") Long id) {
        ResponseDefault responseDefault = null;
        Board board = boardService.findBoardById(id);
        if (board == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("이미 삭제된 글입니다.")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else {
            boardService.removeBoard(id);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("글 삭제 성공!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        }
    }

    //전체 게시판 검색 조회
    @GetMapping("/")
    public ResponseEntity<?> boardList(@Param("page") int page, @Param("search") String search, @Param("type") String type) {
        ResponseDefault responseDefault = null;
        Page<Board> boardList = boardService.boardList(page, search, type);
        Page<ResponseBoard> responseBoard = new ResponseBoard().toDtoList(boardList);
        responseDefault = ResponseDefault.builder()
                .success(true)
                .messege("게시판 조회")
                .data(responseBoard)
                .build();
        return new ResponseEntity<>(responseDefault, HttpStatus.OK);
    }

    //신고 확인
    @GetMapping("/report")
    public ResponseEntity<?> findBoardReportType(@Param("userId") Long userId, @Param("boardId") Long boardId) {
        ResponseDefault responseDefault = null;
        User user = boardService.findUserById(userId);
        Board board = boardService.findBoardById(boardId);

        if (user == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("유효하지 않은 사용자입니다!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else if (board == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("삭제된 게시물입니다!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else {
            Boolean resultType = boardService.findBoardReportType(user, board);
            if (resultType) {
                responseDefault = ResponseDefault.builder()
                        .success(true)
                        .messege("신고 취소하기")
                        .data(true)
                        .build();
            } else {
                responseDefault = ResponseDefault.builder()
                        .success(true)
                        .messege("신고하기")
                        .data(false)
                        .build();
            }
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        }
    }

    //신고하기
    @PostMapping("/report")
    public ResponseEntity<?> boardReportSave(@Param("userId") Long userId, @Param("boardId") Long boardId) {
        ResponseDefault responseDefault = null;
        User user = boardService.findUserById(userId);
        Board board = boardService.findBoardById(boardId);

        if (user == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("유효하지 않은 사용자입니다!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else if (board == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("삭제된 게시물입니다!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else {
            boardService.saveBoardReport(user, board);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("게시물 신고 완료!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        }
    }

    @DeleteMapping("/report")
    public ResponseEntity<?> boardReportRemove(@Param("userId") Long userId, @Param("boardId") Long boardId) {
        ResponseDefault responseDefault = null;
        User user = boardService.findUserById(userId);
        Board board = boardService.findBoardById(boardId);

        if (user == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("유효하지 않은 사용자입니다!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else if (board == null) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("삭제된 게시물입니다!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.BAD_REQUEST);
        } else {
            boardService.removeBoardReport(user, board);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("신고 취소 완료!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestPart("file") MultipartFile file) {
        String imageUrl = null;
        ResponseDefault responseDefault = null;
        try {
//            imageUrl = myService.uploadImage(file);
            s3Service.saveUploadFile(file);
            responseDefault = ResponseDefault.builder()
                    .success(true)
                    .messege("성공")
                    .data(imageUrl)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.OK);
        } catch (IOException e) {
            responseDefault = ResponseDefault.builder()
                    .success(false)
                    .messege("실패")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDefault, HttpStatus.NOT_FOUND);
        }
    }


}
