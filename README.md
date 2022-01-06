# spring-security-practice

스프링 생태계에서 인증과 인가라는 개념을 최대한 쉽고 유연하게 구현할 수 있도록 만들어진 프레임워크

## 인증과 인가
### 인증 Authentication

- 유저가 누구인지 확인하는 절차
- 대표적인 예 : 회원가입하고 로그인 하는 것
    1. 회원가입 ( 아이디, 비번 생성 ⇒ DB에 저장 )
    2. 로그인 ( 아이디, 비번 입력 ⇒ DB에 저장된 아이디의 비번과 일치하는지 비교 )
    3. 일치하면 로그인, 일치하지 않을 경우 로그인 실패
    4. 로그인 성공 시 Access Token 클라이언트에 전송
    5. 최초 로그인 성공 후, 다음부터는 Access Token을 첨부하여 서버에 요청함으로써 매번 로그인하는 과정 생략 가능  
         ⇒ Access token 저장 storage :  Local Storage, Session Storage, Cookie 
        

### 인가 Authorization

- 유저가 요청하는 request를 실행할 수 있는 권한이 있는 유저인가를 확인하는 절차
- 유저에 대한 권한을 허락하는 것
- 인가를 수행하기 위해서는 인증이 선행되어야 한다
    1. 인증 절차를 통해 Access Token 생성 ( 해당 토큰은 사용자의 정보를 담은 상태 ex : user id)
    2. 사용자가 요청을 보낼때 Access Token를 첨부해서 보낸다
    3. 서버는 해당 Access Token을 복호화하고, 정보를 얻음 ( user id )
    4. 얻은 정보( user id )를 사용하여 DB에서 사용자 권한을 확인한다
    5. 권한이 확인되면 해당요청 처리, 권한이 없으면 에러코드 출력

