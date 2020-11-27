# cloud-edge-mgmt

Gedge Management Dashboard

## 사전 환경

* spring boot  설치 필요

  * 언어: java8+
  * 빌드 도구 : maven (https://maven.apache.org/download.cgi)
  * IDE :Intelij

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
    root 선택후 clean -> complie -> install 순으로 update( 권장 )

  ### start project 
  #### application 실행 
    * APIOpenstackApplication
    * ServiceRegistryApplication
    * GatewayApplication 
    * ClientApplication 프로젝트 실행
    
    ->  ClientApplication port를 통해 웹페이지에서 확인 가능 

  
