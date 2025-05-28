/*
This file acts as a centralized API refernce for the front end. It is responsible for making
api calls to the backend.
*/

import axios from 'axios';

const API_URL = 'http://localhost:5454/api';

// Add axios interceptor for JWT token. This ensures that every request includes a token if the user is logged in.
axios.interceptors.request.use(
  (config) => {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user?.token) {
      config.headers.Authorization = `Bearer ${user.token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Registration
export const register = async (userData) => {
  try {
    const response = await axios.post(`${API_URL}/users`, userData);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Login
export const login = async (formData) => {
  try {
    const response = await axios.post(`${API_URL}/auth/login`, formData);
    if (response.data) {
      localStorage.setItem('user', JSON.stringify({ token: response.data }));
    }
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Forgot Password
export const forgotPassword = async (email) => {
  try {
    const response = await axios.post(`${API_URL}/auth/forgot-password`, { registeredEmail: email });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Validate OTP
export const validateOtp = async (email, otp) => {
  try {
    const response = await axios.post(`${API_URL}/auth/validate-otp?email=${email}`, { otp });
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Reset Password
export const resetPassword = async (email, newPassword) => {
  try {
    const response = await axios.post(
      `${API_URL}/auth/reset-password?email=${email}`, 
      { 
        newPassword, 
        confirmPassword: newPassword 
      }
    );
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Change Password (for logged-in users)
export const changePassword = async (userId, currentPassword, newPassword) => {
  try {
    const response = await axios.put(
      `${API_URL}/users/${userId}/change-password`,
      { currentPassword, newPassword }
    );
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Get Current User
export const getCurrentUser = () => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};

// Logout
export const logout = () => {
  localStorage.removeItem('user');
};

// Get User Profile
export const getUserProfile = async (userId) => {
  try {
    const response = await axios.get(`${API_URL}/users/${userId}`);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Update User Profile
export const updateUserProfile = async (userId, userData) => {
  try {
    const response = await axios.put(`${API_URL}/users/${userId}`, userData);
    return response.data;
  } catch (error) {
    throw error;
  }
}; 