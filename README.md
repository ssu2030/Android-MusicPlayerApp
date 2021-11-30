## Android-MusicPlayerApp

2017년도 모바일 프로그래밍 수업 프로젝트

### 개요
구현해야하는 항목
- 앱을 실행 시 mp3 확장자를 가진 파일들을 리스트 뷰로 보여주는데 곡의 앨범과 곡이름 메타데이터 등으로 리스트 업 되도록 만듦
- 곡을 선택하고 곡이 재생되도록 구현 현재 플레이가 시작되면 seek bar로 현재 재생시간 표시 및 seek bar로 음악 재생위치 조작
- 재생/정지/다음곡/이전곡 버튼 이벤트
- 앱의 생명주기를 고려하여 백그라운드에서 서비스가 실행
- 백그라운드에서도 음악 조작 기능

### 파일 설명
##### MainActivity.java
- 음악리스트를 받아오기 위해서 permision 조작 
- 음악리스트를 받아온 후 정보 저장
- 리스트 뷰 구성 

##### PlayMusicActivity.java
- 앨범이미지나 곡 정보 view 구현
- 음악 재생 및 각종 조작을 위한 기능 구현
- Seek Bar 구현 및 조작 시 thread 프로그래밍
- 
##### MusicPlayService.java
- 음악 재생 및 각종 조작을 위한 BroadCast 및 Intent 전송

##### RemoteView.java
- remote view 구현
- remote view에서 BroadCast 및 Intent 전송

### 결과 
##### 앱 최초 실행

![111](https://user-images.githubusercontent.com/31645582/144019362-13c4ce32-a916-47a9-bb7b-9876c3360be4.GIF)
<br>

##### 음악 선택시
![3333](https://user-images.githubusercontent.com/31645582/144019446-04ee4363-04a9-406e-ba06-4f2457313df2.GIF)
![222](https://user-images.githubusercontent.com/31645582/144019452-1f1904c8-12ae-4fd3-ae58-ca095c4ac170.GIF)
<br>

##### 백그라운드 서비스

![444](https://user-images.githubusercontent.com/31645582/144019564-eb992b7b-f066-4549-8c3e-fe87c3ca307d.GIF)



