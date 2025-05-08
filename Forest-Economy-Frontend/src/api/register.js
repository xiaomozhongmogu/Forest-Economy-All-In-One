import request from "@/utils/request";

export const registerApi = (data) => request.post('/register', data);
