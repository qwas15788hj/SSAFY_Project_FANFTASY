## 03/06

• Q1. NFT의 개인키를 우리가(DB 저장) 가지고 있어야하나요?
o A. 키 스토어파일로 저장 가장 좋은건 메타마스크지갑으로 서명하게 하는게 제일이다.
o EIP712 서명을 하면 DB에 저장함 그게 아니라 일반적이면 메타마스크가 알아서 해준다
o 유저의 개인키는 백앤드에 노출이 안되는 게 맞음
o 메타마스크 서명 이용 ⇒ 메타마스크 서명을 DB에 넣는지? O
o EIP712 서명
o 일반적이면 서명하는 순간 바로 실행됨. 우리는 신경 안써도됨 → 메타마스크 코드를 잘 가져와서 써야함

• Q2. 거래 만들 때, 실제 금융거래를 하게되면 법률적인 문제가 발생할 수 있을텐데, 처리를 어떻게 해야하는지 궁금합니다.
o A. 신경을 쓰다가 무산되는 경우가 많다. 최근은 한 회사가 모르겠다 하고 무작정 개발하면 법적 싸움 후 뚫리는 경우가 있다.국내에는 코인이 불법이라 자회사를 통해서 한다 (어차피 metamask가 해외 기업이라 이거 이용해서 큰 문제는 없을 듯)
o 블록체인 기반 포인트라고 우회가 가능
o 가장 개인키를 서비스에서 다루냐 마냐 중앙화되면 유저 프라이빗키를 가지고 있으면 법적 문제가 걸림
o 개인키를 분할해서 가지고있거나, 메타마스크를 이용
o 서명같은거 DB 사용X, 메타마스크 이용
o 개인키를 양쪽에서 분할해서 가지고 있는 방식 쓰거나 메타마스크 쓰긴 함
o
• Q3. NFT 제작 시, 보안체계를 어떤식으로 해야하는지 궁금합니다.
o A. NFT 같은 경우는 컨트랙트 형식으로 발행된다 백앤드나 디비로 넘기는 경우가 많다 부하가 많아서 핵심 로직만 올린다. 대다수의 프로젝트
o 오픈재플린의 핵심코드를 상속 오딧에서 검수?
o 관리자 권한은 멀티시그월렛으로 관리…
o 오딧을 받았을 때 멀티시그월렛을 써라? 개인키를 쪼개서 암호화를 하라는 것으로 보임

• Q4. NFT의 희소성을 일반적으로 어느정도에 측정해야하는지 궁금합니다. (무제한 발급하여 판매하기 VS 극소수의 NFT만 발급하여 희소성을 높이기)
o A. 이부분은 장단점이 있다. 이더락 NFT(100개 제한에 희소성 극악) 커뮤니티 활성화면 많이 발행이 맞음
o 1-2만개 정도 푼다고 하셨는데 무제한은 안되는지? 레벨을 두긴 해야됨-> 혜택을 줘야함

• Q5. 블록체인의 구현과 NFT 발급 구현이 문법적으로 차이가 많은지 궁금합니다.
o 블록체인 구현방법이 여러개
o 컨트렉트 형태로 똑같이 구현

• Q6. 유저간 거래시 발생하는 가스비는 어떻게 처리하는게 일반적인지 궁금합니다.
o A. NFT 거래소 한정 유저 간 거래 할때 오픈씨 할때 거래 내역을 메세지만 가지고 서명을 하면 구매자의 데이터를 받아오고 판매자의 데이터를 받아오고
o 결국 거래를 체결하는 마지막 사람이 최종 결제를 한다.
o NFT를 컨트랙트에 보내고 구매자가 컨트렉트에서 내는게 간단하다. 백앤드에서 검증하는건 힘들다

## 03/07

• ERD 구상 및 설계
• ERD 설계 시 테이블을 기능에 맞추어 세부적으로 나눌 것인지 추후 회의 필요
• ERD 설계 학습 필요

## 03/08

![ERD](./asset/ERD.PNG)
