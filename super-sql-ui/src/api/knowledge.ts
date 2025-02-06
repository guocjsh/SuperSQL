import service from './interceptors';

export function getDocuments(params:any){
    return service.get("/document/getDocuments", {params})
}

export function removeDocument(params:any){
    return service.get("/document/remove", {params})
}

export function trainData(params:any){
    return service.post("/superSql/train",params)
}