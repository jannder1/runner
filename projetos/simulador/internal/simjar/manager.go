
package simjar

import (
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
	"strings"

	"github.com/jannder1/runner/shared/release"
	"github.com/jannder1/runner/simulador/internal/config"
)
var fetchRelease = release.Fetch


func Find(sourceURL string) (string, error) {
	if exe, err := os.Executable(); err == nil {
		jarAoLado := filepath.Join(filepath.Dir(exe), "simulador.jar")
		if fileExists(jarAoLado) {
			return jarAoLado, nil
		}
	}

	if sourceURL != "" {
		fmt.Println("Baixando simulador.jar de --source...")
		if err := download(sourceURL, "", ""); err != nil {
			return "", fmt.Errorf("falha ao baixar simulador.jar de %s: %w", sourceURL, err)
		}
		return config.JarPath(), nil
	}

	local := config.JarPath()
	cfg, err := fetchRelease()
	if err != nil {
		// Sem rede: o cache local, se existir, ainda é utilizável.
		if fileExists(local) {
			return local, nil
		}
		return "", fmt.Errorf(
			"simulador.jar não encontrado localmente e não foi possível consultar o release.json: %w\n"+
				"Conecte-se à internet para o primeiro download ou use --source <url>.",
			err,
		)
	}
	if cfg.Simulador.URL == "" {
		return "", fmt.Errorf("release.json não contém a URL do simulador.jar")
	}

	if fileExists(local) && localVersion() == cfg.Simulador.Version {
		return local, nil
	}

	fmt.Printf("Baixando simulador.jar (versão %s)...\n", cfg.Simulador.Version)
	if err := download(cfg.Simulador.URL, cfg.Simulador.Version, cfg.Simulador.SHA256); err != nil {
		// Falha no download mas com cache presente: degrada para o cache.
		if fileExists(local) {
			fmt.Println("Aviso: falha ao atualizar; usando a cópia local existente.")
			return local, nil
		}
		return "", fmt.Errorf("falha ao baixar simulador.jar: %w", err)
	}
	return local, nil
}

func fileExists(path string) bool {
	info, err := os.Stat(path)
	return err == nil && !info.IsDir()
}

// localVersion lê o marcador de versão do jar em cache; "" se ausente.
func localVersion() string {
	data, err := os.ReadFile(config.VersionPath())
	if err != nil {
		return ""
	}
	return strings.TrimSpace(string(data))
}
func download(url, version, sha256Expected string) error {
	resp, err := http.Get(url) //nolint:noctx
	if err != nil {
		return err
	}
	defer resp.Body.Close()
	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("servidor retornou status %d", resp.StatusCode)
	}

	dest := config.JarPath()
	if err := os.MkdirAll(filepath.Dir(dest), 0755); err != nil {
		return err
	}

	tmp := dest + ".tmp"
	out, err := os.OpenFile(tmp, os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0644)
	if err != nil {
		return err
	}
	h := sha256.New()
	if _, err := io.Copy(out, io.TeeReader(resp.Body, h)); err != nil {
		out.Close()
		os.Remove(tmp)
		return err
	}
	if err := out.Close(); err != nil {
		os.Remove(tmp)
		return err
	}

	if sha256Expected != "" {
		got := hex.EncodeToString(h.Sum(nil))
		if got != sha256Expected {
			os.Remove(tmp)
			return fmt.Errorf("checksum inválido: esperado %s, obtido %s", sha256Expected, got)
		}
	}

	if err := os.Rename(tmp, dest); err != nil {
		os.Remove(tmp)
		return err
	}

	if version != "" {
		if err := os.WriteFile(config.VersionPath(), []byte(version), 0644); err != nil {
			return fmt.Errorf("jar baixado mas não foi possível gravar o marcador de versão: %w", err)
		}
	}
	return nil
}
