<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta http-equiv="X-UA-Compatible" content="ie=edge"/>
    <title>Sign Up</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
          integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC"
          crossorigin="anonymous"/>
    <!-- Font Icon -->
    <link rel="stylesheet" href="fonts/material-icon/css/material-design-iconic-font.min.css"/>
    <!-- Main css -->
    <link rel="stylesheet" href="css/style.css"/>
</head>
<body>
<div class="main">
    <!-- Sign up form -->
    <section class="signup">
        <div class="container">
            <div class="signup-content">
                <div class="signup-form">
                    <h2 class="form-title">Sign up</h2>
                    <form method="post" onsubmit="submitForm(event)"
                          class="register-form" id="register-form">
                        <div class="form-group">
                            <label for="username"><i class="zmdi zmdi-account material-icons-name"></i></label>
                            <input type="text" name="username" id="username" placeholder="Your Username"/>
                        </div>
                        <div class="form-group">
                            <label for="email"><i class="zmdi zmdi-email"></i></label>
                            <input type="email" name="email" id="email" placeholder="Your Email"/>
                        </div>
                        <div class="form-group">
                            <label for="password"><i class="zmdi zmdi-lock"></i></label>
                            <input type="password" name="password" id="password" placeholder="Password"/>
                        </div>
                        <div class="form-group">
                            <!-- Bootstrap styled select -->
                            <select name="usertype" id="usertype" class="form-select" required>
                                <option value="" disabled selected>Select User Type</option>
                                <option value="JobSeeker">Job Seeker</option>
                                <option value="Employer">Employer</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <input type="checkbox" name="agree-term" id="agree-term" class="agree-term"/>
                            <label for="agree-term" class="label-agree-term"><span><span></span></span>I agree all
                                statements in <a href="#" class="term-service">Terms of service</a></label>
                        </div>
                        <div class="form-group form-button">
                            <input type="submit" name="signup" id="signup" class="form-submit" value="Register"/>
                        </div>
                    </form>
                </div>
                <div class="signup-image">
                    <figure>
                        <img src="images/signup-image.jpg" alt="sing up image"/>
                    </figure>
                    <a href="login.jsp" class="signup-image-link">I am already a member</a>
                </div>
            </div>
        </div>
    </section>
</div>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<!-- JS -->
<script src="vendor/jquery/jquery.min.js"></script>
<script src="js/main.js"></script>
<script>
function submitForm(event) {
    event.preventDefault(); // Prevent the default form submission

    // Get form data
    const formData = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        usertype: document.getElementById('usertype').value
    };

    // Send form data as JSON
    fetch('/api/users/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
    .then(response => response.text()) // Use response.text() for HTML response
    .then(data => {
        // Display the HTML response
        document.body.innerHTML = data;
    });
}
</script>
</body>
</html>
