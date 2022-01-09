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

<br>


## Spring Security 내부구조

```java
SecurityContext context = SecurityContextHolder.getContext(); // Security Context
Authentication authentication = context.getAuthentication(); // authentication
authentication.getPrincipal();
authentication.getAuthorities();
authentication.getCredentials();
authentication.getDetails();
authentication.isAuthenticated();
```

- SecurityContext
    - 접근 주체와 인증에 대한 정보를 담고있는 Context (즉 Authentication을 담고있음)
- Authentication
    - Principal과 GrantAuthority를 제공. 인증이 이루어 지면 해당 Authentication이 저장됨
    - `Authentication.isAuthenticated();` ⇒ true이면 인증 완료
- Principal
    - 유저에 해당하는 정보
    - 대부분의 경우 Principal로 UserDetails를 반환한다
- GrantAuthority
    - prefix로 ‘ROLE_’ 이 붙으며 ROLE_ADMIN, ROLE_USER등 Principal이 가지고 있는 권한을 나타낸다
    - 인증 이후에 인가를 할 때 사용한다
    - 권한은 여러개 일수 있기 때문에 Collection<GrantedAuthority>형태로 제공한다

<br>
    
### Thread Local

- SecurityContextHolder.getContext() 했을 때 해당 요청을 진행한 유저를 알아보고 해당 유저에 맞는 SecurityContext를 반할할 수 있는 이유는 Thread Local을 사용했기 때문
- mvc 기반으로 프로젝트를 반드는 경유에는 요청 1개에 Thread 1개가 생성된다. 이때 Thread Local을 사용하면 Thread마다 고유한 공간을 만들수 있고 그 곳에 SecurityContext를 저장할 수 있다
    - 요청 1개 → Thread 1개 → SecurityContext 1개 
    ⇒ SecurityContext는 요청마다 독립적으로 관리될 수 있다
- Spring Security의 기본적인 Security Context 관리 전략은 ThreadLocal을 사용하는ThreadLocalSecurityContextHolderStrategy
- thread가 달라지면 제대로 된 인증 정보를 가져올 수 없다

<br>
    
### Password Encoder

- 해시 알고리즘 사용 - 복호화가 거의 불가능
- 회원가입시 password를 암호화 하여 저장 → 로그인할 때 password가 들어오면 같은 해시함수로 암호화
→ 비교 → 동일하면 같은 password로 인지

