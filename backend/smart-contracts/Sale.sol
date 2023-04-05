// SPDX-License-Identifier: MIT
pragma solidity ^0.8.4;

import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/math/SafeMath.sol";
import "@openzeppelin/contracts-upgradeable/token/ERC721/IERC721ReceiverUpgradeable.sol";
import "contracts/NFT.sol";

contract Sale is Ownable{
    address public admin; //관리자 계정
    address public nftContractAddress; //nft스마트 컨트랙트 주소

    // 생성자에 의해 정해지는 값
    address payable public seller; //판매자
    uint256 public price; //가격
    uint256 public tokenId; //판매NFT ID
    bool public isEnded; //완료여부
    bool public isCanceled; //취소여부
    MyToken public NFTContract;
    uint256 public endedAt; //판매시간
    address private _buyer;//구매자 지갑주소

    event SaleEnded(address buyer, uint256 price);

    constructor(
        address payable _seller,
        uint256 _tokenId,
        uint256 _price,
        address _nftContractAddress,
        address _admin
    ) {
        require(_price >= 0, "Price must be higher than 0.");
        require(MyToken(_nftContractAddress).ownerOf(_tokenId) == _seller, "Seller is not owner.");
        tokenId = _tokenId;
        price = _price;
        seller = _seller;
        isEnded = false;
        isCanceled = false;
        nftContractAddress = _nftContractAddress;
        NFTContract = MyToken(_nftContractAddress);
        // _NFTContract.approve(admin,_tokenId);
        admin = _admin;
    }

    function purchase() payable public returns(bool) {
        require(!isEnded, "This sale is already ended.");
        require(NFTContract.ownerOf(tokenId) == seller);
        require(msg.value == price, "Buyer's balance is exhausted.");
        //판매자에게서 구매자의 Money전송
        seller.transfer(msg.value);
        _buyer = msg.sender;
        // end();
        emit SaleEnded(msg.sender, price);
        return true;
    }

    //판매가격 조회
    function getFinalPrice() public view returns(uint256) {
        return price;
    }

    //판매종료함수
    function end() public onlyOwner{
        endedAt = block.timestamp;
        isEnded = true;
    }
    
    //판매 취소 함수
    function cancelSales() public {
        require(!isEnded, "This sale is already ended.");
        require(!isCanceled, "This sale is already canceled.");
        // Sale을 취소 상태로 설정
        isCanceled = true;
    }

    //거래 시간
    function getEndedAt() public view returns(uint256) {
        require(isEnded, "This sale is not ended yet.");
        return endedAt;
    }
}