package com.jobPortal.utility;

public class Data {

    public static String htmlContent = """
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <title>JobHook - OTP Verification</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        background-color: #f4f4f7;
        color: #333333;
        margin: 0;
        padding: 0;
      }
      .container {
        max-width: 600px;
        margin: 40px auto;
        background-color: #ffffff;
        border-radius: 8px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        padding: 30px;
      }
      .header {
        text-align: center;
        padding-bottom: 20px;
      }
      .header h1 {
        color: #3b82f6;
        margin: 0;
        font-size: 28px;
      }
      .content {
        font-size: 16px;
        line-height: 1.6;
        color: #444444;
      }
      .otp-box {
        text-align: center;
        background-color: #f0f4ff;
        padding: 20px;
        margin: 20px 0;
        border-radius: 6px;
        font-size: 24px;
        font-weight: bold;
        letter-spacing: 4px;
        color: #1e40af;
      }
      .footer {
        margin-top: 30px;
        font-size: 14px;
        text-align: center;
        color: #888888;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <div class="header">
        <h1>JobHook</h1>
        <p>OTP Verification</p>
      </div>
      <div class="content">
        <p>Hi <strong>%s</strong>,</p>
        <p>We received a request to verify your email address. Please use the OTP below to proceed:</p>
        <div class="otp-box">%s</div>
        <p>This OTP is valid for <strong>5 minutes</strong>. Please do not share it with anyone.</p>
        <p>If you didn't request this, you can safely ignore this email.</p>
        <p>Thanks,<br />The JobHook Team</p>
      </div>
      <div class="footer">
        <p>&copy; 2025 JobHook. All rights reserved.</p>
      </div>
    </div>
  </body>
</html>
""";


}
