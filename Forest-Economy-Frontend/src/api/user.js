import request from "@/utils/request";

export const deleteByIdApi = (id) => request.delete(`/home/${id}`);
