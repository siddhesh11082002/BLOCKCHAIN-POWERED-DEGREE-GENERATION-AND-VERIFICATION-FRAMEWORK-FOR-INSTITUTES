// src/services/api.jsx
const API_URL = 'http://localhost:8081/api';

const api = {
  get: async (endpoint, customHeaders = {}) => {
    try {
      const headers = {
        'Content-Type': 'application/json',
        ...customHeaders
      };
      
      const response = await fetch(`${API_URL}${endpoint}`, {
        method: 'GET',
        headers,
        credentials: 'include',
        mode: 'cors'
      });

      // Handle non-2xx responses
      if (!response.ok) {
        await handleErrorResponse(response);
      }

      // Return the parsed JSON
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      } else {
        return { success: true, status: response.status };
      }
    } catch (error) {
      console.error(`GET ${endpoint} error:`, error);
      throw error;
    }
  },

  post: async (endpoint, data, customHeaders = {}) => {
    try {
      let headers = {
        'Content-Type': 'application/json',
        ...customHeaders
      };
      
      // Remove Content-Type for FormData
      if (data instanceof FormData) {
        delete headers['Content-Type'];
      }
      
      console.log(`Making POST request to ${API_URL}${endpoint}`);
      
      const response = await fetch(`${API_URL}${endpoint}`, {
        method: 'POST',
        headers,
        credentials: 'include',
        mode: 'cors',
        body: data instanceof FormData ? data : JSON.stringify(data)
      });

      // Handle non-2xx responses
      if (!response.ok) {
        await handleErrorResponse(response);
      }

      // Return the parsed JSON
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      } else {
        return { success: true, status: response.status };
      }
    } catch (error) {
      console.error(`POST ${endpoint} error:`, error);
      throw error;
    }
  },

  patch: async (endpoint, data, customHeaders = {}) => {
    try {
      const headers = {
        'Content-Type': 'application/json',
        ...customHeaders
      };
      
      const response = await fetch(`${API_URL}${endpoint}`, {
        method: 'PATCH',
        headers,
        credentials: 'include',
        mode: 'cors',
        body: JSON.stringify(data)
      });

      // Handle non-2xx responses
      if (!response.ok) {
        await handleErrorResponse(response);
      }

      // Return the parsed JSON
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      } else {
        return { success: true, status: response.status };
      }
    } catch (error) {
      console.error(`PATCH ${endpoint} error:`, error);
      throw error;
    }
  },
  
  delete: async (endpoint, customHeaders = {}) => {
    try {
      const headers = {
        'Content-Type': 'application/json',
        ...customHeaders
      };
      
      const response = await fetch(`${API_URL}${endpoint}`, {
        method: 'DELETE',
        headers,
        credentials: 'include',
        mode: 'cors'
      });

      // Handle non-2xx responses
      if (!response.ok) {
        await handleErrorResponse(response);
      }

      // Return the parsed JSON
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      } else {
        return { success: true, status: response.status };
      }
    } catch (error) {
      console.error(`DELETE ${endpoint} error:`, error);
      throw error;
    }
  }
};

// Helper function for error handling
async function handleErrorResponse(response) {
  let errorMessage;
  try {
    const errorData = await response.json();
    errorMessage = errorData.error || errorData.message || `Request failed with status ${response.status}`;
  } catch (e) {
    // If the response cannot be parsed as JSON
    errorMessage = `Request failed with status ${response.status}: ${response.statusText}`;
  }
  throw new Error(errorMessage);
}

export default api;
