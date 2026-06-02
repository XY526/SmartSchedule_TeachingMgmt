import request from '@/utils/request'; // Assuming you have a request utility

// API call for AI analysis
export const analyzeData = (data) => {
    return request({
        url: '/api/ai/analyze',
        method: 'POST',
        data: data,
        timeout: 30000 // Increase timeout to 30 seconds
    });
};

// API call for AI chat
export const chatWithAI = (message) => {
    return request({
        url: '/api/ai/chat',
        method: 'POST',
        data: { message: message },
        timeout: 30000 // Increase timeout to 30 seconds
    });
}; 