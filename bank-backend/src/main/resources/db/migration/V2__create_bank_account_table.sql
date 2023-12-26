CREATE TABLE bank_account (
    id UUID PRIMARY KEY,
    user_id UUID,
    balance DECIMAL(19, 4),
    FOREIGN KEY (user_id) REFERENCES _user(id)
);
INSERT INTO bank_account (id, user_id, balance) VALUES
    ('707596ef-0e14-435b-b618-421066f7b701', '126422c9-592e-4b65-8b84-f14dcdac7cef', '1000.00'),
    ('239a61c4-6406-48d7-8266-b07f87ee9550', '6a971fb8-48b3-4b60-8248-a5d60c7e421c', '500.00');