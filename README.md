# 스마트 아기침대 어플리케이션
* viewpager, TabLayout
* bluetooth commutication
* ListView, Graph, Chart, seekBar etc
* Database

## 홈 - Home
#### 블루투스 연결
<img src="https://user-images.githubusercontent.com/33975923/77205912-341f5d00-6b39-11ea-91a5-d805f60978fd.gif" width=240px height=480px/>

#### 아기상태 조회
##### 아이 상태 변화 시 notification으로 알림
* 수면 → 기저귀
<img src="https://user-images.githubusercontent.com/33975923/78008669-e7c6ef00-737a-11ea-9dd4-68531c0568e3.gif" width=240px height=480px/>

* 수면 → 기상
** 수면시간 기록과 데이터베이스 저장
<img src="https://user-images.githubusercontent.com/33975923/78009298-c7e3fb00-737b-11ea-8c85-7009c59525ac.gif" width=240px height=480px/>

## 침대설정 - Setting

#### 침대 설정
* seekbar - 침대 상태 설정
<img src="https://user-images.githubusercontent.com/33975923/78009902-af281500-737c-11ea-84d7-6bf9ede29eb6.gif" width=240px height=480px/>
* pie chart - 기상 후 아기 행동 패턴 조회\

## 수면일기 - Sleepdiary
* 수면기록 조회
<img src="https://user-images.githubusercontent.com/33975923/78015795-de428480-7384-11ea-8984-9d582f26937c.gif" width=320px height=640px/>
<img src="https://user-images.githubusercontent.com/33975923/78015787-dd115780-7384-11ea-8619-4c9560b02b9e.gif" width=320px height=640px/>

* 수면기록 저장
<img src="https://user-images.githubusercontent.com/33975923/78015801-df73b180-7384-11ea-9f90-f527e76c8a32.gif" width=320px height=640px/>
<img src="https://user-images.githubusercontent.com/33975923/78015796-dedb1b00-7384-11ea-9334-95f82fcf3eae.gif" width=320px height=640px/>

## 자장가 - PlayLullaby
ListView 사용. 내부 Database 연동
* 자동재생\
자장가는 선호도가 높은 순으로 정렬.
자장가 자동재생(하단의 ▶ 버튼 선택) 시 선호도 순으로 재생되며,\
음악 하나가 끝날 때마다 H/W에서 플래그를 전송해 다음 음악을 재생한다.
<img src="https://user-images.githubusercontent.com/33975923/78016366-aab42a00-7385-11ea-8fbb-c72a0d55c4cb.gif" width=320px height=640px/>

* 선택재생\
사용자가 직접 자장가를 선택해 재생가능.\
사용자가 선택한 곡 재생이 끝나면 다시 선호도 순 재생.
<img src="https://user-images.githubusercontent.com/33975923/78016582-f961c400-7385-11ea-89d7-b56fcbdb5d2a.gif" width=320 height=640px/>

* 갱신\
아기가 잠들면 아기가 잠들기 전에 들었던 자장가 세곡을 아이가 잠드는데 유의미한 자장가라고 판단,\
아기가 잠들기전에 들었던 자장가 세곡의 선호도 값을 증가시켜 리스트를 갱신.
<img src="https://user-images.githubusercontent.com/33975923/78016728-2ada8f80-7386-11ea-91b3-cff26f0ff3d9.gif" width=320px height=640px/>
