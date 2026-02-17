<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'PHPMailer/src/Exception.php';
require 'PHPMailer/src/PHPMailer.php';
require 'PHPMailer/src/SMTP.php';

$mail = new PHPMailer(true);

try {
    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Username = 'fulllswingg@gmail.com';
    $mail->Password = 'rfopnvzrpgxltxxj';
    $mail->SMTPSecure = 'tls';
    $mail->Port = 587;

    $mail->setFrom('YOUR_GMAIL@gmail.com', 'DrugSearch');
    $mail->addAddress('YOUR_GMAIL@gmail.com');

    $mail->isHTML(true);
    $mail->Subject = 'Test Mail';
    $mail->Body = '<h3>PHPMailer is working 🎉</h3>';

    $mail->send();
    echo "Mail sent successfully";

} catch (Exception $e) {
    echo "Mailer Error: " . $mail->ErrorInfo;
}
