export default function Common(){
  const useEmpty= (data:any)=>{
    return data===''?true:data instanceof Array&&data.length===0?true:data??true
  } 

  return {
    useEmpty
  }
}




// //判断为空
// const useEmpty= (data:any)=>{
//   return data===''?true:data instanceof Array&&data.length===0?true:data??true
// }

// //判断多个值为空(未深度检测)
// const useEmptys =(data:any,type:string)=>{
//   let arr = []
//   data instanceof Object ? arr = Object.values(data):data instanceof Array ? arr = data : []
//   arr = arr.map((item:any)=>useEmpty(item))
//   return type =='&&' ? arr.every((item:any)=>item===true) : arr.some((item:any)=>item===true)
// }
// // 注意：暴露的时候使用export{函数名}
// export default {
//   useEmpty,
//   useEmptys
// }
  