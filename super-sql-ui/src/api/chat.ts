import post from "@/api/request";
import {AxiosProgressEvent, GenericAbortSignal} from "axios";

export function fetchChatProcess<T = any>(
    params: {
        data?: any
        signal?: GenericAbortSignal
        onDownloadProgress?: (progressEvent: AxiosProgressEvent) => void
        beforeRequest?: () => void
        afterRequest?: () => void
    },

) {
    const data = params.data;

    return post<T>({
        url: 'super/chat',
        data,
        signal: params.signal,
        onDownloadProgress: params.onDownloadProgress,
        beforeRequest:params.beforeRequest,
        afterRequest:params.afterRequest
    })
}