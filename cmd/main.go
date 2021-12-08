package main

import (
  // "crypto/aes"
  // "crypto/cipher"
  // "crypto/rand"
  "encoding/base64"
  "fmt"
  "os"
  "net/http"
  "github.com/gin-gonic/gin"
)

func main() {
  // cipher
  // key := []byte("FLAG{here_15_5ecret_ky4_12345ab}")

  // web server
  r := gin.Default()

  r.POST("/greet", func(c *gin.Context) {
    param_name := c.PostForm("name")
    // decrypt
    enc_name_byte, err := base64.StdEncoding.DecodeString(param_name)
    if err != nil {
      c.String(http.StatusBadRequest, "failed in base64.decode")
      return
    }

    res := []byte(fmt.Sprintf("Hello, %s!", enc_name_byte))
    res_encoded := base64.StdEncoding.EncodeToString(res)
    c.String(http.StatusOK, res_encoded)
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
