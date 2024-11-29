# 📗 図書管理サービス bmb(bookmybook) 
<img src="https://i.esdrop.com/d/f/8vpuvvaxBD/BN88D9TPzZ.jpg" width="100%"/>

**bmb project back-end repository**
- Site URL : https://bmb-project.vercel.app/
- Notion URL : https://observant-cheetah-dc4.notion.site/bmb-project-0e4cf89411834ef3bfd297b304bcd9ea

<br/>

## 目次 ##
1. [開発期間](#開発期間)
2. [メンバー](#メンバー)
3. [システムアーキテクチャ](#システムアーキテクチャ)
4. [技術スタックとツール](#技術スタックとツール)
5. [API](#API)
6. [ERD](#ERD)
7. [機能詳細](#機能詳細)
8. [サービス画面](#サービス画面)

<br/>

## 開発期間 ##
2024.07.24 - 2024.09.03(41日)

<br/>

## メンバー ##
| 魚蓮眞(オ・ヨンジン, Yeonjin Eo) / BE                                                                                  | 金宣佑(キム・ソヌ, Seonu Kim) / BE                                      |
|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| <div align="center"><img src="https://i.esdrop.com/d/f/8vpuvvaxBD/2MgJoAIXQ3.jpg" width="450"/></div>  | <div align="center"><img src="https://i.esdrop.com/d/f/8vpuvvaxBD/grnFyCpRL4.jpg" width="190"/></div>  |
| wks1211@gmail.com                                       | bareulgod2466@gmail.com                                            |
| https://github.com/EoYeonjin                            | https://github.com/HauOmochikaeri                                  |
| BEの技術スタック選定、BEの開発環境セッティング、</br>API仕様書作成、API開発、BEをAWSのEC2とS3でデプロイ、</br>BEのCI/CD構築、ERD作成 | BEの技術スタック選定、</br>DB設計、API開発  |

<br/>

## システムアーキテクチャ ##
<img src="https://i.esdrop.com/d/f/8vpuvvaxBD/3oz37jYJQO.jpg" width="800"/>

<br/>

## 技術スタックとツール ##
### フロントエンド ###
Typescript, React, Next.js, Npm, Zustand, Axios, TanStack Query, React hook form, Zod, Tailwind css, Shadcn/ui, ESLint, Prettier, Husky & LintStaged, Vercel, VS Code

### バクエンド ###
Java, Spring Boot, Hibernate, Spring Data JPA, Spring Security, JWT, Gradle, h2, Maria DB, AWS EC2, S3, Route 53, IntelliJ

### ツール ###
Git, Notion, Slack

<br/>

## API ##
*詳しい情報はリンクをクリックします
### USER API ###
| 機能       | Method | URL | 担当者 | 開発期間 |
|------------|--------|-----------| ----------- | ------------ |
| [**アカウント作成**](https://www.notion.so/c151da28fc8d47f9b20bf1995af96c95) | POST | /user/signup | 魚蓮眞(オ・ヨンジン) | 2024年8月12日 → 2024年8月13日 |
| [**ログイン**](https://www.notion.so/c8a568420a0f4665a2ff2c8da2b68df4) | POST | /user/signin | 魚蓮眞(オ・ヨンジン) | 2024年8月13日 → 2024年8月18日 | 
| [**サインアウト**](https://www.notion.so/dd8810c12ba64bd1a7b16711cc4c1c84) | POST | /user/signout | 魚蓮眞(オ・ヨンジン) | 2024年9月2日 → 2024年9月2日 | 
| [**会員別貸出リスト照会**](https://www.notion.so/0154b8d0a8154e60872e14f1707307da) | GET | /user/loan | 魚蓮眞(オ・ヨンジン) | 2024年8月26日 → 2024年8月26日 | 
| [**会員別お気に入り</br>リスト照会**](https://www.notion.so/9fb3daa31a834d97b8b031be474b759f) | GET | /user/wish | 魚蓮眞(オ・ヨンジン) | 2024年8月27日 → 2024年8月27日 | 

### BOOK API ###
| 機能       | Method | URL | 担当者 | 開発期間 |
|------------|--------|-----------| ----------- | ------------ |
| [**図書リスト**](https://www.notion.so/8d6f9414d2434908b016f9fc16199c71) | GET | /books | 魚蓮眞(オ・ヨンジン) | 2024年8月20日 → 2024年8月22日 |
| [**図書情報**](https://www.notion.so/a20afcbb92094391bae81b66f16ed804) | GET | /books/{isbn} | 金宣佑(キム・ソヌ) | 2024年8月24日 → 2024年8月25日 |
| [**図書別お気に入りリスト照会**](https://www.notion.so/2331b0a3dcf94783915bcc078e5cd2a4) | GET | /books/{isbn}/wish | 魚蓮眞(オ・ヨンジン) | 2024年8月28日 → 2024年8月28日 |
| [**お気に入り登録**](https://www.notion.so/121c74624e9d4fad9695746779d0f05d) | POST | /books/{isbn}/wish | 魚蓮眞(オ・ヨンジン) | 2024年8月28日 → 2024年8月28日 |
| [**お気に入り取り消し**](https://www.notion.so/12d1f0fe5e084a02a09b35f8609049fa) | DELETE | /books/{isbn}/wish | 魚蓮眞(オ・ヨンジン) | 2024年8月28日 → 2024年8月28日 |

### LOAN API ###
| 機能       | Method | URL | 担当者 | 開発期間 |
|------------|--------|-----------| ----------- | ------------ |
| [**貸出**](https://www.notion.so/afec36e95de24f42a72a2618f87a39c7) | POST | /loan | 魚蓮眞(オ・ヨンジン) | 2024年8月22日 → 2024年8月24日 |
| [**返却**](https://www.notion.so/81a55677ae7c4b1985bd458fd857ec8b) | PUT | /loan | 魚蓮眞(オ・ヨンジン) | 2024年8月22日 → 2024年8月24日 |

### ADMIN API ###
| 機能       | Method | URL | 担当者 | 開発期間 |
|------------|--------|-----------| ----------- | ------------ |
| [**図書登録**](https://www.notion.so/9b358a3f4ba040beb6ed84fbd2d0d23d) | POST | /admin/books | 金宣佑(キム・ソヌ) | 2024年8月16日 → 2024年8月18日 |
| [**図書リスト（アドミン）**](https://www.notion.so/6c8e31da922349478b8b7e984d6606b5) | GET | /admin/books | 魚蓮眞(オ・ヨンジン) | 2024年8月24日 → 2024年8月25日 |
| [**図書詳細情報（アドミン）**](https://www.notion.so/7394a072c03c40ae9ee75b499c82b81f) | GET | /admin/books/{isbn} | 金宣佑(キム・ソヌ) | 2024年9月3日 → 2024年9月3日 |
| [**図書削除**](https://www.notion.so/14c43611ab81438cba6722b6782cb86b) | DELETE | /admin/books/{isbn} | 金宣佑(キム・ソヌ) | 2024年8月26日 → 2024年8月27日 |
| [**会員リスト（アドミン）**](https://www.notion.so/14c43611ab81438cba6722b6782cb86b) | GET | /admin/users | 魚蓮眞(オ・ヨンジン) | 2024年11月6日 → 2024年11月7日 |

### Token API ###
| 機能       | Method | URL | 担当者 | 開発期間 |
|------------|--------|-----------| ----------- | ------------ |
| [**Refresh Token Rotation**](https://www.notion.so/API-3ddf796b76c9410eb0da71231557d632) | POST | /auth | 漁蓮眞(オ・ヨンジン) | 2024年8月26日 → 2024年8月27日 |

<br/>

## [ERD](https://www.notion.so/DB-6717f586b5da4d618735694179a44bbb) ##
### 概念的データモデリング ###
<img src="https://i.esdrop.com/d/f/8vpuvvaxBD/wrWQgNzkwR.png" width="100%"/>

### 論理的データモデリング ###
<img src="https://i.esdrop.com/d/f/8vpuvvaxBD/KP44TH2zoh.png" width="100%"/>

<br/>

## 機能詳細 ##
### USER ###
**アカウント作成**
- id, password, nicknameを入力して会員登録
- idとnicknameは重複をチェック
- dbにはid、username、password、role、登録日が保存
- idがadminで始まる場合、admin権限付与
- DBにパスワードが保存される際、spring securityによりパスワードを暗号化
- validation check
  - idは英語+数字4字以上10字以下
  - nicknameは日本語と英数字のみ2字以上10字以下
  - passwordは英語+数字or特殊文字8字以上15字以下

**ログイン**
- JWT(Access Token, Refresh Token)発給
- ログイン時に入力したpasswordを暗号化し、dbに保存された暗号化されたpasswordと比較
- トークンにはuser_idとroleの情報が挿入
- AccessTokenはheaderに、RefreshTokenはCookieに保存
- ‘USER’, ‘ADMIN’でRoleについて機能およびページ制限

**サインアウト**
- サインアウトするとcookieにある access token, user info, refresh tokenを削除

### BOOK ###
**図書リスト(USER,ADMIN)、お気に入りリスト、貸出リスト(USER)**
- キーワード(ALL)、タイトル、作家、出版社カテゴリで区分して図書を検索可能
- 1ページに12個ずつ、数字をクリックすると該当ページに遷移、＜＞でページ5個ずつ1グループずつ移動可能

**図書登録(ADMIN)**
- title、isbn、thumbnail、author_name、publisher_name、published_date、descriptionを作成し、form-dataで受信してDBに保存
- isbnは重複をチェック
- thumbnailで受け取ったimageをS3サーバーに保存し、リンクを受けてdbに保存
- validation
  - isbnは13字の数字のみ
  - published_dateは有効な日付形式か、今日以降の日付は登録不可
  - descriptionは1000字以下

**図書削除(ADMIN)**
- 貸出リストがない場合、図書削除が可能
- 本が存在しない場合、エラーとして処理

### SECURITY ###
**ページ接近**
- アカウント作成, ログイン
  - 誰でもアクセス可能
  
- サインアウト, 会員別貸出リスト照会, 会員別お気に入りリスト照会, 図書リスト, 図書情報, 図書別お気に入りリスト照会, お気に入り登録, お気に入り取り消し, 貸出, 返却
  - ログイン時に発行されたaccesstokenから抽出したroleがuserまたはadminの場合のみアクセス可能
    
- 図書登録(ADMIN), 図書リスト(ADMIN), 図書詳細(ADMIN) ,図書削除(ADMIN)
  - ログイン時に発行されたaccesstokenから抽出したroleがadminの場合のみアクセス可能
    
**RTR**
- ログインされていますが、tokenが満了した場合、自動的に新しいtokenを発行します

<br/>

## 開発する時に気を使った点 ##
- セキュリティを高めるため、RTRとSpring security、パスワード暗号化機能を追加
- 機能別にController、Entity、Respositoryなどでクラスを区別
- 必要最小限のエラーのみを処理
- 開発時には便利なh2DBを、配布時にはMariaDBを使用
- role権限を区分してuserとadminの役割を区分したページを表示できるようにしました

<br/>


