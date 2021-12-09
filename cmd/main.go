package main

import (
  "crypto/aes"
  "crypto/cipher"
  "crypto/rand"
  "encoding/base64"
  "fmt"
  "io"
  "os"
  "net/http"
  "github.com/gin-gonic/gin"
)


// cipher
var KEY = []byte("FLAG{5ecret_ke4}")

func encrypt(s string) (string, error) {
  c, err := aes.NewCipher(KEY)
  if err != nil {
    return "", err
  }

  gcm, err := cipher.NewGCM(c)
  if err != nil {
    return "", err
  }

  nonce := make([]byte, gcm.NonceSize())
  if _, err = io.ReadFull(rand.Reader, nonce); err != nil {
    return "", err
  }

  out := gcm.Seal(nonce, nonce, []byte(s), nil)
  out_enc := base64.StdEncoding.EncodeToString(out)
  return out_enc, err
}

func decrypt(param_name string) ([]byte, error) {
  enc_name_byte, err := base64.StdEncoding.DecodeString(param_name)
  if err != nil {
    return []byte{}, err
  }

  c, err := aes.NewCipher(KEY)
  if err != nil {
    return []byte{}, err
  }

  gcm, err := cipher.NewGCM(c)
  if err != nil {
    return []byte{}, err
  }

  nonceSize := gcm.NonceSize()
  if len(enc_name_byte) < nonceSize {
    return []byte{}, fmt.Errorf("too short param")
  }

  nonce, cipherText := enc_name_byte[:nonceSize], enc_name_byte[nonceSize:]
  plain, err := gcm.Open(nil, nonce, cipherText, nil)
  if err != nil {
    return []byte{}, err
  }

  return plain, err
}

func main() {

  // web server
  r := gin.Default()

  r.POST("/greet", func(c *gin.Context) {
    param_name := c.PostForm("name")
    decrypted, err := decrypt(param_name)
    if err != nil {
      c.String(http.StatusBadRequest, err.Error())
      return
    }

    res := []byte(fmt.Sprintf("Hello, %s!", decrypted))

    enc, err := encrypt(string(res))
    if err != nil {
      c.String(http.StatusBadRequest, err.Error())
      return
    }

    c.String(http.StatusOK, enc)
  })

  r.GET("/", func(c *gin.Context) {
    c.String(http.StatusOK, "Hello, World!")
  })

  port := os.Getenv("PORT")
  if port == "" {
    port = "8080"
  }

  if err := r.Run(":" + port); err != nil {
    panic(err)
  }
}
