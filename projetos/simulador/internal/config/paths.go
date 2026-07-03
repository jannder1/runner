
package config

import (
	"path/filepath"

	shared "github.com/jannder1/runner/shared/config"
)

func JarPath() string {
	dir, _ := shared.HubSaudeDir()
	return filepath.Join(dir, "simulador.jar")
}


func PidPath() string {
	dir, _ := shared.HubSaudeDir()
	return filepath.Join(dir, "simulador.pid")
}



func VersionPath() string {
	dir, _ := shared.HubSaudeDir()
	return filepath.Join(dir, "simulador.version")
}
