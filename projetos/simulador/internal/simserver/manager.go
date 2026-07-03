
package simserver

import (
	"crypto/tls"
	"encoding/json"
	"fmt"
	"net"
	"net/http"
	"os"
	"time"

	"github.com/jannder1/runner/simulador/internal/config"
)

type ProcessInfo struct {
	PID  int `json:"pid"`
	Port int `json:"port"`
}


var PidFilePath = ""


var dialHost = "localhost"

const httpTimeout = 2 * time.Second


var httpClient = &http.Client{
	Timeout: httpTimeout,
	Transport: &http.Transport{
		TLSClientConfig: &tls.Config{InsecureSkipVerify: true}, //nolint:gosec
	},
}

func pidFile() string {
	if PidFilePath != "" {
		return PidFilePath
	}
	return config.PidPath()
}

func baseURL(port int, path string) string {
	return fmt.Sprintf("https://%s:%d%s", dialHost, port, path)
}


func WriteProcessInfo(info ProcessInfo) error {
	data, err := json.Marshal(info)
	if err != nil {
		return err
	}
	return os.WriteFile(pidFile(), data, 0644)
}


func ReadProcessInfo() (*ProcessInfo, error) {
	data, err := os.ReadFile(pidFile())
	if err != nil {
		return nil, err
	}
	var info ProcessInfo
	if err := json.Unmarshal(data, &info); err != nil {
		return nil, fmt.Errorf("arquivo de PID corrompido: %w", err)
	}
	return &info, nil
}


func ClearProcessInfo() {
	_ = os.Remove(pidFile())
}


func IsResponding(port int) bool {
	resp, err := httpClient.Get(baseURL(port, "/api/info"))
	if err != nil {
		return false
	}
	resp.Body.Close()
	return true
}


type Info struct {
	Name    string `json:"name"`
	Version string `json:"version"`
}


func Probe(port int) (*Info, error) {
	resp, err := httpClient.Get(baseURL(port, "/api/info"))
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("status inesperado %d em /api/info", resp.StatusCode)
	}
	var info Info
	if err := json.NewDecoder(resp.Body).Decode(&info); err != nil {
		return nil, fmt.Errorf("resposta de /api/info inválida: %w", err)
	}
	return &info, nil
}

func RequestShutdown(port int) error {
	resp, err := httpClient.Post(baseURL(port, "/shutdown"), "application/json", nil)
	if err != nil {
		return err
	}
	defer resp.Body.Close()
	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("POST /shutdown retornou %d", resp.StatusCode)
	}
	return nil
}


func WaitUntilDown(port int, timeout time.Duration) error {
	deadline := time.Now().Add(timeout)
	url := baseURL(port, "/api/info")
	for time.Now().Before(deadline) {
		time.Sleep(500 * time.Millisecond)
		resp, err := httpClient.Get(url)
		if err != nil {
			return nil
		}
		resp.Body.Close()
	}
	return fmt.Errorf("simulador ainda responde após %s", timeout)
}


func IsPortFree(port int) bool {
	ln, err := net.Listen("tcp", fmt.Sprintf(":%d", port))
	if err != nil {
		return false
	}
	_ = ln.Close()
	return true
}
