<template>
  <div style="
    width: 100%;
    height: 100vh;
    /* background-color: white; */
    padding-top: 10px;
    text-align: center;
    /* display: flex; */
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;">
    <span class="minting-title">
      NFT 생성
    </span>
    <div style="width:400px; justify-content: left;">
      <div >
        <label for="mintingFile">
          <img v-if="imageUrl" :src="imageUrl" style="width:400px; height: 200px; border-radius: 15px; object-fit: contain;"/>
          <video v-else-if="videoUrl" style="width:400px; height: 200px; border-radius: 15px; object-fit: contain;" controls >
            <source :src="videoUrl" type="video/mp4" />
          </video>
          <div v-else style="display: flex; justify-content: center; align-items: center; width:400px; height: 200px; border:2px solid #DAD2E9; border-radius: 15px; ">
            <v-icon icon="mdi-upload" style="color:#9B7CF8; font-size: 60px;"></v-icon>
          </div>
        </label>
        <input id="mintingFile" type="file" ref="fileInput" style="display: none;" @change="previewFile" accept="image/*, video/*" />
      </div>
      
      <!-- <br> -->
      <!-- <br> -->
      <!-- <label for="minting-file" style="margin-right:10px">파일선택</label>   -->
      <!-- <input v-on:change="previewFiles(this.files)" type="file" accept="image/*, video/*, audio/*" style="border:solid"> -->
      <!-- <input type="file" @change="handleFileUpload" accept="image/*, video/*, audio/*"/> -->
      <!-- <br> -->
      <br>
      <label for="minting-title">제목</label>
      <input id="minting-title" v-model="title" type="text" class="minting-input" style="height: 40px;" placeholder="제목을 입력해주세요.">
      <br>
      <br>
      <div style="position:relative">
        <label for="minting-content" style="position: absolute; margin-bottom:100px; top: 10px; left:20px">내용</label>
        <textarea v-model="content" name="minting-content" id="" cols="30" rows="5" class="minting-input" style="padding-top: 10px" placeholder="상세내용을 입력해주세요."></textarea>
      </div>
      <br>
      <label for="">발매 갯수</label>
      <input v-model="totalNum" type="text" class="minting-input" style="height:40px;" placeholder="발매할 NFT수를 입력해주세요.">
      <br>
      <br>
      <label for="">발매가격</label>
      <input v-model="originPrice" type="text" class="minting-input" style="height:40px;" placeholder="판매가격을 입력해주세요.">
      <br>
      <br>
      <label for="">판매종료일</label>
      <input v-model="endDate" type="date" class="minting-input" style="height:40px; padding-right:10px" >
  
      <br>
      <br>
    </div>
    <!-- <label for="">파일 타입</label>
    <input type="combobox" style="border:solid"> -->
    <button @click="this.addNFT()" class="minting-button">
      생성 시작
    </button>
  </div>
</template>

<script>
import VueCookies from "vue-cookies"
import Swal from 'sweetalert2'
import router from "@/router"

export default {
  name: "MarketAddView",
  data() {
    return {
      file: null,
      title:"",
      content:"",
      totalNum:"",
      originPrice:"",
      regArtistId: VueCookies.get("userId"),
      fileType:"",
      selectedFile: null,
      endDate: null,
      now: Date.now(), 
      role: null,
      // a: Date.

      imageUrl: null,
      videoUrl: null,
    }
  },
  created(){
    this.getUserDetail()
    this.isLogin()
  },
  methods: {
    async getUserDetail(){
      try{
        const result = await this.$store.dispatch('userDetail')

        console.log("result : " + result)
        this.role = this.$store.state.role;
        console.log(this.role)
        this.isArtist()
      }
      catch (err){
        console.log(err)
      }
    },

    async isLogin(){
      if (VueCookies.get("nickname") === null){
        this.mem = true
        await Swal.fire({
          title: "로그인 되지 않았습니다.",
          text: "로그인 후 이용해주세요",
          icon: "warning" //"info,success,warning,error" 중 택1
        })
        router.go(-1)
      } else {
        this.mem = false
      }
    },

    isArtist(){
      if (this.role != 'artist') {
        Swal.fire({
          title: "접근 불가",
          text: "권한이 없습니다!",
          icon: "warning" //"info,success,warning,error" 중 택1
        })
        router.go(-1)
      }
    },

    handleFileUpload(event) {
      this.file = event.target.files[0];
      console.log('this.file :', this.file);
      console.log('this,file.type.substring(0, 5) :', this.file.type.substring(0, 5))
      this.fileType = this.file.type.substring(0, 5)
    },
    addNFT() {
      const file = this.file
      const title = this.title
      const content = this.content
      const totalNum = this.totalNum
      const originPrice = this.originPrice
      const regArtistId = this.regArtistId
      let fileType = this.fileType
      const endDate = this.endDate + "T00:00:00"
      console.log(endDate)

      const payload = {
        file,
        title,
        content,
        totalNum,
        originPrice,
        regArtistId,
        fileType,
        endDate,
      }
      console.log("----------------------")
      console.log(payload)

      this.$store.dispatch("addNFT", payload)
    },

    previewFile(event) {
      this.file = event.target.files[0];
      console.log('this.file :', this.file);
      console.log('this,file.type.substring(0, 5) :', this.file.type.substring(0, 5))
      this.fileType = this.file.type.substring(0, 5)

      const pre_file = event.target.files[0];

      if (pre_file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.readAsDataURL(pre_file);
        reader.onload = () => {
          this.imageUrl = reader.result;
        };
        this.videoUrl = null

      } else if (pre_file.type.startsWith('video/')) {
        const reader = new FileReader();
        reader.readAsDataURL(pre_file);
        reader.onload = () => {
          this.videoUrl = reader.result;
        };
        this.imageUrl = null
      }
    },
  },
}
</script>

<style>
.minting-title{
  font-size: 45px;
  font-weight: bold;
  color: #6a3fc1;
}
.minting-input{
  border:2px solid #DAD2E9; 
  margin-left:12px; 
  padding-left:10px; 
  border-radius: 15px;
}
.minting-button{
  width: 20vw;
  height: 8vh;
  border-radius: 15px;
  font-size: 2vw;
  font-weight: bold;
  border: 1px #9B7CF8 solid;
  color: #9B7CF8; 
}
.minting-button:hover{
  background-color: #9B7CF8;
  color: white;
}
</style>