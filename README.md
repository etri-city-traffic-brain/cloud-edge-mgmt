# cloud-edge-mgmt

UNIQ Management Dashboard
* 도시교통 브레인 시스템의 클라우드 인프라 및 엣지 서버 운영 관리를 위한 대시보드


## 사전 환경

* spring boot 설치 필요

  * 언어: java8+
  * 빌드 도구 : maven (https://maven.apache.org/download.cgi)
  * IDE :Intelij
  
  #### Database 설정 
    * postgresql 및 mysql 설치 후 해당 내용 core-db -> DataSourceConfigure.java에 입력
    
    * (postgresql)cloud credential table  
    ![image](https://user-images.githubusercontent.com/23303734/204425577-7b03da7a-8f0a-45dd-ab0d-a5e0a02c6dad.png)
    * (mysql)edge server table  
    ![image](https://user-images.githubusercontent.com/23303734/204425837-4990d451-7350-4ec4-9a04-8f01761fce1b.png)

  ### spring project setting 

  * spring boot project 생성
  * http://start.spring.io/ 에서 생성 또는 Intelij 내 프로젝트 생성
  * import Project
    * maven 플러그인 설치 되어 있어야 함 

  ### maven plugin 추가 설치가 필요한 경우
  
  *  Maven dependency를 추가를 위해 pom.xml 에 해당 Library를 추가하여 설치 가능
  *  프로젝트 생성 시 Maven > importing 에서 Import Maven projects automatically 체크를 통한 자동 update 하도록 설정 가능 

  ### Generate sources and update folder for all projects
  
  * code update 후 maven update 
  * maven update 
    root 선택후 clean -> compile -> install 순으로 update( 권장 )

  ### start project 
        
  #### application 실행 
    * APIOpenstackApplication
    * ServiceRegistryApplication
    * GatewayApplication 
    * ClientApplication 프로젝트 실행
    
    ->  ClientApplication port를 통해 웹페이지에서 확인 가능
    
* linux 환경

  * java version 1.8.0 설치

  #### application 실행
    * 각 모듈 jar 파일 생성 및 실행
    * "java -jar 모듈.jar" 명령어 실행


## 실행 화면

* 로그인  
![image](https://user-images.githubusercontent.com/23303734/204427476-c7501fc0-4ce5-4136-9b03-68d4acfcb0b2.png)
  
* 대시보드  
![대시보드](https://user-images.githubusercontent.com/23303734/204427708-68bef427-2167-4787-ba8c-ccce5b2d2494.png)

* 오픈스택 관리  
![image](https://user-images.githubusercontent.com/23303734/204427863-ec39ce4f-ce2e-4512-b0ef-feee9434ed3f.png)

* 모니터링  
![모니터링](https://user-images.githubusercontent.com/23303734/204427770-77b054a7-c4d9-4bf6-9f85-e86fa663e4e4.png)

* 엣지 단말 조회  
![엣지](https://user-images.githubusercontent.com/23303734/204427777-d55100f5-98cf-4220-923a-fe49b4678291.png)


  
